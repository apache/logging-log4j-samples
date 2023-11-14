/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.parser.ParseException;
import org.apache.logging.log4j.core.util.Closer;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.apache.logging.log4j.core.tools.picocli.CommandLine.Command;
import org.apache.logging.log4j.core.tools.picocli.CommandLine.Option;
import org.apache.logging.log4j.message.EntryMessage;

/**
 * Listens for Log4j events on a TCP server socket and passes them on to Log4j.
 *
 * @param <T>
 *        The kind of input stream read
 * @see #main(String[])
 */
public class TcpSocketServer<T extends InputStream> extends AbstractSocketServer<T> {

    @Command(name = "TcpSocketServer")
    protected static class CommandLineArguments extends AbstractSocketServer.CommandLineArguments {

        @Option(names = { "--backlog", "-b" }, description = "Server socket backlog. Must be a positive integer.")
        // Same default as ServerSocket
        private int backlog = 50;

        int getBacklog() {
            return backlog;
        }

        void setBacklog(final int backlog) {
            this.backlog = backlog;
        }
    }

    /**
     * Thread that processes the events.
     */
    private class SocketHandler extends Log4jThread {

        private final T inputStream;
        private final Socket socket;

        private volatile boolean shutdown = false;

        public SocketHandler(final Socket socket) throws IOException {
            this.socket = socket;
            this.inputStream = logEventInput.wrapStream(socket.getInputStream());
        }

        @Override
        public void run() {
            final EntryMessage entry = logger.traceEntry();
            boolean closed = false;
            try {
                try {
                    while (!shutdown) {
                        logEventInput.logEvents(inputStream, TcpSocketServer.this);
                    }
                } catch (final EOFException e) {
                    closed = true;
                } catch (final OptionalDataException e) {
                    logger.error("OptionalDataException eof=" + e.eof + " length=" + e.length, e);
                } catch (final IOException e) {
                    logger.error("IOException encountered while reading from socket", e);
                } catch (ParseException e) {
                    logger.error("ParseException encountered while reading from socket", e);
                }
                if (!closed) {
                    Closer.closeSilently(inputStream);
                }
            } finally {
                handlers.remove(Long.valueOf(getId()));
            }
            logger.traceExit(entry);
        }

        public void shutdown() {
            this.shutdown = true;
            if (socket != null) {
                Closer.closeSilently(socket);
            }
            interrupt();
        }
    }

    /**
     * Creates a socket server that reads JSON log events.
     *
     * @param port
     *        The port number, or 0 to automatically allocate a port number.
     * @return a new a socket server
     * @throws IOException
     *         if an I/O error occurs when opening the socket.
     */
    public static TcpSocketServer<InputStream> createJsonSocketServer(final int port) throws IOException {
        LOGGER.entry("createJsonSocketServer", port);
        final TcpSocketServer<InputStream> socketServer = new TcpSocketServer<>(port, new JsonInputStreamLogEventBridge());
        return LOGGER.exit(socketServer);
    }

    /**
     * Creates a socket server that reads JSON log events.
     *
     * @param port
     *        The port number, or 0 to automatically allocate a port number.
     * @param backlog
     *        The server socket backlog.
     * @param localBindAddress
     *        The local InetAddress the server will bind to
     * @return a new a socket server
     * @throws IOException
     *         if an I/O error occurs when opening the socket.
     * @since 2.9
     */
    public static TcpSocketServer<InputStream> createJsonSocketServer(final int port, final int backlog,
            final InetAddress localBindAddress) throws IOException {
        LOGGER.entry("createJsonSocketServer", port, backlog, localBindAddress);
        final TcpSocketServer<InputStream> socketServer = new TcpSocketServer<>(port, backlog, localBindAddress,
                new JsonInputStreamLogEventBridge());
        return LOGGER.exit(socketServer);
    }

    /**
     * Creates a socket server that reads XML log events.
     *
     * @param port
     *        The port number, or 0 to automatically allocate a port number.
     * @return a new a socket server
     * @throws IOException
     *         if an I/O error occurs when opening the socket.
     */
    public static TcpSocketServer<InputStream> createXmlSocketServer(final int port) throws IOException {
        LOGGER.entry(port);
        final TcpSocketServer<InputStream> socketServer = new TcpSocketServer<>(port, new XmlInputStreamLogEventBridge());
        return LOGGER.exit(socketServer);
    }

    /**
     * Creates a socket server that reads XML log events.
     *
     * @param port
     *        The port number, or 0 to automatically allocate a port number.
     * @param backlog
     *        The server socket backlog.
     * @param localBindAddress
     *        The local InetAddress the server will bind to
     * @return a new a socket server
     * @throws IOException
     *         if an I/O error occurs when opening the socket.
     * @since 2.9
     */
    public static TcpSocketServer<InputStream> createXmlSocketServer(final int port,
        final int backlog, final InetAddress localBindAddress
    ) throws IOException {
        LOGGER.entry(port);
        final TcpSocketServer<InputStream> socketServer = new TcpSocketServer<>(port, backlog, localBindAddress,
                new XmlInputStreamLogEventBridge());
        return LOGGER.exit(socketServer);
    }

    /**
     * Main startup for the server. Run with "--help" for to print command line help on the console.
     *
     * @param args
     *        The command line arguments.
     * @throws Exception
     *         if an error occurs.
     */
    public static void main(final String[] args) throws Exception {
        CommandLineArguments cla = CommandLine.populateCommand(new CommandLineArguments(), args);
        if (cla.isHelp() || cla.backlog < 0 || cla.getPort() < 0) {
            CommandLine.usage(cla, System.err);
            return;
        }
        if (cla.getConfigLocation() != null) {
            ConfigurationFactory.setConfigurationFactory(new ServerConfigurationFactory(cla.getConfigLocation()));
        }
        final TcpSocketServer<InputStream> socketServer = TcpSocketServer.createJsonSocketServer(
                cla.getPort(), cla.getBacklog(), cla.getLocalBindAddress());
        final Thread serverThread = socketServer.startNewThread();
        if (cla.isInteractive()) {
            socketServer.awaitTermination(serverThread);
        }
    }

    private final ConcurrentMap<Long, SocketHandler> handlers = new ConcurrentHashMap<>();

    private final ServerSocket serverSocket;

    /**
     * Constructor.
     *
     * @param port
     *        The port number, or 0 to automatically allocate a port number.
     * @param backlog
     *        The server socket backlog.
     * @param localBindAddress
     *        The local InetAddress the server will bind to
     * @param logEventInput
     *        the log even input
     * @throws IOException
     *         if an I/O error occurs when opening the socket.
     * @since 2.7
     */
    @SuppressWarnings("resource")
    public TcpSocketServer(final int port, final int backlog, final InetAddress localBindAddress, final LogEventBridge<T> logEventInput) throws IOException {
        this(port, logEventInput, new ServerSocket(port, backlog, localBindAddress));
    }

    /**
     * Constructor.
     *
     * @param port
     *         The port number, or 0 to automatically allocate a port number.
     * @param logEventInput
     *        the log even input
     * @throws IOException
     *         if an I/O error occurs when opening the socket.
     */
    @SuppressWarnings("resource")
    public TcpSocketServer(final int port, final LogEventBridge<T> logEventInput) throws IOException {
        this(port, logEventInput, new ServerSocket(port));
    }

    /**
     * Constructor.
     *
     * @param port
     *        to listen.
     * @param logEventInput
     *        the log even input
     * @param serverSocket
     *        the socket server
     * @throws IOException
     *         if an I/O error occurs when opening the socket.
     */
    public TcpSocketServer(final int port, final LogEventBridge<T> logEventInput, final ServerSocket serverSocket)
            throws IOException {
        super(port, logEventInput);
        this.serverSocket = serverSocket;
    }

    /**
     * Accept incoming events and processes them.
     */
    @Override
    public void run() {
        final EntryMessage entry = logger.traceEntry();
        while (isActive()) {
            if (serverSocket.isClosed()) {
                return;
            }
            try {
                // Accept incoming connections.
                logger.debug("Listening for a connection {}...", serverSocket);
                @SuppressWarnings("resource") // clientSocket is closed during SocketHandler shutdown
                final Socket clientSocket = serverSocket.accept();
                logger.debug("Accepted connection on {}...", serverSocket);
                logger.debug("Socket accepted: {}", clientSocket);
                clientSocket.setSoLinger(true, 0);

                // accept() will block until a client connects to the server.
                // If execution reaches this point, then it means that a client
                // socket has been accepted.

                final SocketHandler handler = new SocketHandler(clientSocket);
                handlers.put(Long.valueOf(handler.getId()), handler);
                handler.start();
            } catch (final IOException e) {
                if (serverSocket.isClosed()) {
                    // OK we're done.
                    logger.traceExit(entry);
                    return;
                }
                logger.error("Exception encountered on accept. Ignoring. Stack trace :", e);
            }
        }
        for (final Map.Entry<Long, SocketHandler> handlerEntry : handlers.entrySet()) {
            final SocketHandler handler = handlerEntry.getValue();
            handler.shutdown();
            try {
                handler.join();
            } catch (final InterruptedException ignored) {
                // Ignore the exception
            }
        }
        logger.traceExit(entry);
    }

    /**
     * Shutdown the server.
     *
     * @throws IOException if the server socket could not be closed
     */
    @Override
    public void shutdown() throws IOException {
        final EntryMessage entry = logger.traceEntry();
        setActive(false);
        //Thread.currentThread().interrupt();
        serverSocket.close();
        logger.traceExit(entry);
    }

    @Override
    public String toString() {
        return "TcpSocketServer [serverSocket=" + serverSocket + ", handlers=" + handlers + ", logEventInput="
                + logEventInput + "]";
    }
}

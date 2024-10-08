/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.samples.android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int logLevelIdx;

    //Logger log = LogManager.getLogger(MainActivity.class);
    Logger log = LogManager.getRootLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*// Manually read in log4j2.properties
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        try {
            InputStream inputStream = getAssets().open("log4j2.properties");
            ConfigurationSource source = new ConfigurationSource(inputStream);
            context.start(ConfigurationFactory.getInstance().getConfiguration(context, source));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*// Explicitly set the logging level for this logger to INFO
        //Configurator.setLevel(log, Level.INFO);
        LoggerConfig loggerConfig = context.getConfiguration().getLoggerConfig(log.getName());
        loggerConfig.setLevel(Level.INFO);*/

        //
        // UI bindings
        //
        //TextView displaying Log Level
        TextView logLevelTxt = findViewById(R.id.logLevelTxt);
        logLevelTxt.setText(log.getLevel().name());

        //Change log level
        Button setLogLevelBtn = findViewById(R.id.setLogLevelBtn);
        setLogLevelBtn.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Set log level");

            String[] logLevels = new String[]{"OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"};
            logLevelIdx = Arrays.asList(logLevels).indexOf(log.getLevel().name());

            alertDialog.setSingleChoiceItems(logLevels, logLevelIdx, (dialog, which) -> logLevelIdx = which);
            alertDialog.setPositiveButton("Select", (dialog, which) -> {
                Configurator.setLevel(log, Level.valueOf(Arrays.asList(logLevels).get(logLevelIdx)));
                logLevelTxt.setText(log.getLevel().name());
                dialog.dismiss();
            });
            alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            alertDialog.create();
            alertDialog.show();
        });

        //Log messages
        Button fatalLogBtn = findViewById(R.id.fatalLogBtn);
        fatalLogBtn.setOnClickListener(v -> log.fatal("Current log level is: " + log.getLevel().name()));

        Button errorLogBtn = findViewById(R.id.errorLogBtn);
        errorLogBtn.setOnClickListener(v -> log.error("Current log level is: " + log.getLevel().name()));

        Button warnLogBtn = findViewById(R.id.warnLogBtn);
        warnLogBtn.setOnClickListener(v -> log.warn("Current log level is: " + log.getLevel().name()));

        Button infoLogBtn = findViewById(R.id.infoLogBtn);
        infoLogBtn.setOnClickListener(v -> log.info("Current log level is: " + log.getLevel().name()));

        Button debugLogBtn = findViewById(R.id.debugLogBtn);
        debugLogBtn.setOnClickListener(v -> log.debug("Current log level is: " + log.getLevel().name()));

        Button traceLogBtn = findViewById(R.id.traceLogBtn);
        traceLogBtn.setOnClickListener(v -> log.trace("Current log level is: " + log.getLevel().name()));
    }
}
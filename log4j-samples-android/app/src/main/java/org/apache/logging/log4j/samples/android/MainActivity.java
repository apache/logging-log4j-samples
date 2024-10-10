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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = LogManager.getLogger(MainActivity.class);
    static final int[] buttonIds = {
        R.id.fatalLogBtn, R.id.errorLogBtn, R.id.warnLogBtn, R.id.infoLogBtn, R.id.debugLogBtn, R.id.traceLogBtn
    };
    /**
     * All log levels
     */
    private static final List<Level> logLevels = Arrays.asList(
            Level.OFF, Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE, Level.ALL);

    private int logLevelIdx;
    private final Map<View, Level> buttonToLevel = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.info("Creating Main Activity...");
        setContentView(R.layout.activity_main);

        // TextView displaying Log Level
        TextView logLevelTxt = findViewById(R.id.logLevelTxt);
        logLevelTxt.setText(logger.getLevel().name());

        // Change log level
        Button setLogLevelBtn = findViewById(R.id.setLogLevelBtn);
        setLogLevelBtn.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Set log level");

            logLevelIdx = logLevels.indexOf(logger.getLevel());

            alertDialog.setSingleChoiceItems(
                    logLevels.stream().map(Level::name).toArray(String[]::new),
                    logLevelIdx,
                    (dialog, which) -> logLevelIdx = which);
            alertDialog.setPositiveButton("Select", (dialog, which) -> {
                Configurator.setLevel(logger, logLevels.get(logLevelIdx));
                logLevelTxt.setText(logger.getLevel().name());
                dialog.dismiss();
            });
            alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            alertDialog.create();
            alertDialog.show();
        });

        // Log messages
        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            // The `OFF` level should not have a button
            buttonToLevel.put(button, logLevels.get(i + 1));
            button.setOnClickListener(this::onLevelButtonClick);
        }
        logger.info("Main Activity created.");
    }

    void onLevelButtonClick(View view) {
        Level level = buttonToLevel.get(view);
        if (level != null) {
            logger.log(level, "Hello {}!", level);
        }
    }
}
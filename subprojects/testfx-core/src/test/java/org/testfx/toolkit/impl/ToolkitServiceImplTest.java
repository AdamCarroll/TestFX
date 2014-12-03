/*
 * Copyright 2013-2014 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European
 * Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work
 * except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the Licence for the specific language governing permissions
 * and limitations under the Licence.
 */
package org.testfx.toolkit.impl;

import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.toolkit.ToolkitService;
import org.testfx.toolkit.StageFuture;
import org.testfx.toolkit.ToolkitApplication;

import static org.testfx.util.WaitForAsyncUtils.sleep;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import static org.testfx.util.WaitForAsyncUtils.waitForAsyncFx;

public class ToolkitServiceImplTest {

    //---------------------------------------------------------------------------------------------
    // FIELDS.
    //---------------------------------------------------------------------------------------------

    public static Stage primaryStage;
    public static ToolkitService toolkitService;

    //---------------------------------------------------------------------------------------------
    // FIXTURE METHODS.
    //---------------------------------------------------------------------------------------------

    @BeforeClass
    public static void setupSpec() throws Exception {
        StageFuture primaryStageFuture = ToolkitApplication.primaryStageFuture;
        Class<? extends Application> toolkitApplication = ToolkitApplication.class;
        toolkitService = new ToolkitServiceImpl(
            new ApplicationLauncherImpl(), new ApplicationServiceImpl()
        );

        primaryStage = waitFor(10, TimeUnit.SECONDS,
            toolkitService.setupPrimaryStage(primaryStageFuture, toolkitApplication)
        );
    }

    @Before
    public void setup() throws Exception {
        waitForAsyncFx(2000, () -> {
            primaryStage.show();
        });
    }

    @After
    public void cleanup() throws Exception {
        //waitForAsyncFx(2000, () -> primaryStage.hide());
    }

    //---------------------------------------------------------------------------------------------
    // FEATURE CLASSES.
    //---------------------------------------------------------------------------------------------

    @Test
    public void should_construct_application() throws Exception {
        printCurrentThreadName("should_construct_application()");
        Application application = waitFor(5, TimeUnit.SECONDS,
            toolkitService.setupApplication(primaryStage, FixtureApplication.class)
        );
        sleep(2, TimeUnit.SECONDS);
    }

    @Test
    public void should_construct_scene() throws Exception {
        printCurrentThreadName("should_construct_scene");
        Scene scene = waitFor(5, TimeUnit.SECONDS,
            toolkitService.setupScene(primaryStage, () -> new FixtureScene())
        );
        sleep(2, TimeUnit.SECONDS);
    }

    //---------------------------------------------------------------------------------------------
    // FIXTURE CLASSES.
    //---------------------------------------------------------------------------------------------

    public static class FixtureApplication extends Application {
        public void init() throws Exception {
            printCurrentThreadName("Application#init()");
        }

        public void start(Stage primaryStage) throws Exception {
            printCurrentThreadName("Application#start()");
            Parent parent = new StackPane(new Label(getClass().getSimpleName()));
            Scene scene = new Scene(parent, 400, 200);
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public void stop() throws Exception {
            printCurrentThreadName("Application#stop()");
        }
    }

    public static class FixtureScene extends Scene {
        public FixtureScene() {
            super(new Region(), 400, 200);
            printCurrentThreadName("Scene#init()");
            Parent parent = new StackPane(new Label(getClass().getSimpleName()));
            setRoot(parent);
        }
    }

    //---------------------------------------------------------------------------------------------
    // HELPER METHODS.
    //---------------------------------------------------------------------------------------------

    private static void printCurrentThreadName(String methodSignature) {
        String threadName = Thread.currentThread().getName();
        System.out.println(methodSignature + " in '" + threadName + "'");
    }

}

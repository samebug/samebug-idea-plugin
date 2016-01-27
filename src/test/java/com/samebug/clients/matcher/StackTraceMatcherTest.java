package com.samebug.clients.matcher;

import com.samebug.clients.api.StackTraceListener;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class StackTraceMatcherTest {
    @Test
    public void testAndroidOutput() throws IOException {
        InputStream is = getClass().getResourceAsStream("/androidoutput.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));

        StackTraceListener listener = Mockito.mock(StackTraceListener.class);
        StackTraceMatcher matcher = new StackTraceMatcher(listener);

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                matcher.line(line);
            }
            matcher.end();

            Mockito.verify(listener).stacktraceFound("                                       android.content.res.Resources$NotFoundException: Resource ID #0x10801a3\n" +
                    "                                           at android.content.res.Resources.getValue(Resources.java:1193)\n" +
                    "                                           at android.content.res.Resources.getDrawable(Resources.java:744)\n" +
                    "                                           at android.content.res.Resources.getDrawable(Resources.java:718)\n" +
                    "                                           at com.android.internal.os.ZygoteInit.preloadDrawables(ZygoteInit.java:446)\n" +
                    "                                           at com.android.internal.os.ZygoteInit.preloadResources(ZygoteInit.java:381)\n" +
                    "                                           at com.android.internal.os.ZygoteInit.preload(ZygoteInit.java:251)\n" +
                    "                                           at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:608)");

            Mockito.verify(listener).stacktraceFound("                                                            java.lang.IllegalArgumentException: No such service: ComponentInfo{com.android.server/com.android.server.MountServiceIdler}\n" +
                    "                                                                at com.android.server.job.JobSchedulerService$JobSchedulerStub.enforceValidJobRequest(JobSchedulerService.java:632)\n" +
                    "                                                                at com.android.server.job.JobSchedulerService$JobSchedulerStub.schedule(JobSchedulerService.java:666)\n" +
                    "                                                                at android.app.JobSchedulerImpl.schedule(JobSchedulerImpl.java:42)\n" +
                    "                                                                at com.android.server.MountServiceIdler.scheduleIdlePass(MountServiceIdler.java:96)\n" +
                    "                                                                at com.android.server.MountService.handleSystemReady(MountService.java:636)\n" +
                    "                                                                at com.android.server.MountService.access$500(MountService.java:112)\n" +
                    "                                                                at com.android.server.MountService$MountServiceHandler.handleMessage(MountService.java:526)\n" +
                    "                                                                at android.os.Handler.dispatchMessage(Handler.java:102)\n" +
                    "                                                                at android.os.Looper.loop(Looper.java:135)\n" +
                    "                                                                at android.os.HandlerThread.run(HandlerThread.java:61)");

            Mockito.verify(listener).stacktraceFound("                                                             android.view.InflateException: Binary XML file line #34: Error inflating class android.widget.FrameLayout\n" +
                    "                                                                 at android.view.LayoutInflater.createView(LayoutInflater.java:629)\n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneLayoutInflater.onCreateView(PhoneLayoutInflater.java:55)\n" +
                    "                                                                 at android.view.LayoutInflater.onCreateView(LayoutInflater.java:678)\n" +
                    "                                                                 at android.view.LayoutInflater.createViewFromTag(LayoutInflater.java:737)\n" +
                    "                                                                 at android.view.LayoutInflater.rInflate(LayoutInflater.java:802)\n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:500)\n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:410)\n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:361)\n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.generateLayout(PhoneWindow.java:3254)\n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.installDecor(PhoneWindow.java:3321)\n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.getDecorView(PhoneWindow.java:1812)\n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindowManager.addStartingWindow(PhoneWindowManager.java:1728)\n" +
                    "                                                                 at com.android.server.wm.WindowManagerService$H.handleMessage(WindowManagerService.java:7298)\n" +
                    "                                                                 at android.os.Handler.dispatchMessage(Handler.java:102)\n" +
                    "                                                                 at android.os.Looper.loop(Looper.java:135)\n" +
                    "                                                                 at android.os.HandlerThread.run(HandlerThread.java:61)\n" +
                    "                                                                 at com.android.server.ServiceThread.run(ServiceThread.java:46)\n" +
                    "                                                              Caused by: java.lang.reflect.InvocationTargetException\n" +
                    "                                                                 at java.lang.reflect.Constructor.newInstance(Native Method)\n" +
                    "                                                                 at java.lang.reflect.Constructor.newInstance(Constructor.java:288)\n" +
                    "                                                                 at android.view.LayoutInflater.createView(LayoutInflater.java:603)\n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneLayoutInflater.onCreateView(PhoneLayoutInflater.java:55) \n" +
                    "                                                                 at android.view.LayoutInflater.onCreateView(LayoutInflater.java:678) \n" +
                    "                                                                 at android.view.LayoutInflater.createViewFromTag(LayoutInflater.java:737) \n" +
                    "                                                                 at android.view.LayoutInflater.rInflate(LayoutInflater.java:802) \n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:500) \n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:410) \n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:361) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.generateLayout(PhoneWindow.java:3254) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.installDecor(PhoneWindow.java:3321) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.getDecorView(PhoneWindow.java:1812) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindowManager.addStartingWindow(PhoneWindowManager.java:1728) \n" +
                    "                                                                 at com.android.server.wm.WindowManagerService$H.handleMessage(WindowManagerService.java:7298) \n" +
                    "                                                                 at android.os.Handler.dispatchMessage(Handler.java:102) \n" +
                    "                                                                 at android.os.Looper.loop(Looper.java:135) \n" +
                    "                                                                 at android.os.HandlerThread.run(HandlerThread.java:61) \n" +
                    "                                                                 at com.android.server.ServiceThread.run(ServiceThread.java:46) \n" +
                    "                                                              Caused by: android.content.res.Resources$NotFoundException: Unable to find resource ID #0x0\n" +
                    "                                                                 at android.content.res.Resources.getResourceName(Resources.java:1961)\n" +
                    "                                                                 at android.content.res.Resources.loadDrawableForCookie(Resources.java:2325)\n" +
                    "                                                                 at android.content.res.Resources.loadDrawable(Resources.java:2265)\n" +
                    "                                                                 at android.content.res.TypedArray.getDrawable(TypedArray.java:743)\n" +
                    "                                                                 at android.widget.FrameLayout.<init>(FrameLayout.java:113)\n" +
                    "                                                                 at android.widget.FrameLayout.<init>(FrameLayout.java:101)\n" +
                    "                                                                 at android.widget.FrameLayout.<init>(FrameLayout.java:97)\n" +
                    "                                                                 at java.lang.reflect.Constructor.newInstance(Native Method) \n" +
                    "                                                                 at java.lang.reflect.Constructor.newInstance(Constructor.java:288) \n" +
                    "                                                                 at android.view.LayoutInflater.createView(LayoutInflater.java:603) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneLayoutInflater.onCreateView(PhoneLayoutInflater.java:55) \n" +
                    "                                                                 at android.view.LayoutInflater.onCreateView(LayoutInflater.java:678) \n" +
                    "                                                                 at android.view.LayoutInflater.createViewFromTag(LayoutInflater.java:737) \n" +
                    "                                                                 at android.view.LayoutInflater.rInflate(LayoutInflater.java:802) \n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:500) \n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:410) \n" +
                    "                                                                 at android.view.LayoutInflater.inflate(LayoutInflater.java:361) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.generateLayout(PhoneWindow.java:3254) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.installDecor(PhoneWindow.java:3321) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindow.getDecorView(PhoneWindow.java:1812) \n" +
                    "                                                                 at com.android.internal.policy.impl.PhoneWindowManager.addStartingWindow(PhoneWindowManager.java:1728) \n" +
                    "                                                                 at com.android.server.wm.WindowManagerService$H.handleMessage(WindowManagerService.java:7298) \n" +
                    "                                                                 at android.os.Handler.dispatchMessage(Handler.java:102) \n" +
                    "                                                                 at android.os.Looper.loop(Looper.java:135) \n" +
                    "                                                                 at android.os.HandlerThread.run(HandlerThread.java:61) \n" +
                    "                                                                 at com.android.server.ServiceThread.run(ServiceThread.java:46) ");

            Mockito.verify(listener).stacktraceFound("                                                                  java.util.concurrent.TimeoutException: Cannot get spooler!\n" +
                    "                                                                      at com.android.server.print.RemotePrintSpooler.bindLocked(RemotePrintSpooler.java:376)\n" +
                    "                                                                      at com.android.server.print.RemotePrintSpooler.getRemoteInstanceLazy(RemotePrintSpooler.java:352)\n" +
                    "                                                                      at com.android.server.print.RemotePrintSpooler.removeObsoletePrintJobs(RemotePrintSpooler.java:287)\n" +
                    "                                                                      at com.android.server.print.UserState.removeObsoletePrintJobs(UserState.java:164)\n" +
                    "                                                                      at com.android.server.print.PrintManagerService$PrintManagerImpl$1.run(PrintManagerService.java:127)\n" +
                    "                                                                      at android.os.Handler.handleCallback(Handler.java:738)\n" +
                    "                                                                      at android.os.Handler.dispatchMessage(Handler.java:95)\n" +
                    "                                                                      at android.os.Looper.loop(Looper.java:135)\n" +
                    "                                                                      at android.os.HandlerThread.run(HandlerThread.java:61)");

            Mockito.verify(listener).stacktraceFound("                                                                                      java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.android.trivialdrivesample/com.example.android.trivialdrivesample.MainActivity}: java.lang.RuntimeException: Please put your app's public key in MainActivity.java. See README.\n" +
                    "                                                                                          at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2255)\n" +
                    "                                                                                          at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2317)\n" +
                    "                                                                                          at android.app.ActivityThread.access$800(ActivityThread.java:143)\n" +
                    "                                                                                          at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1258)\n" +
                    "                                                                                          at android.os.Handler.dispatchMessage(Handler.java:102)\n" +
                    "                                                                                          at android.os.Looper.loop(Looper.java:135)\n" +
                    "                                                                                          at android.app.ActivityThread.main(ActivityThread.java:5070)\n" +
                    "                                                                                          at java.lang.reflect.Method.invoke(Native Method)\n" +
                    "                                                                                          at java.lang.reflect.Method.invoke(Method.java:372)\n" +
                    "                                                                                          at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:836)\n" +
                    "                                                                                          at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:631)\n" +
                    "                                                                                       Caused by: java.lang.RuntimeException: Please put your app's public key in MainActivity.java. See README.\n" +
                    "                                                                                          at com.example.android.trivialdrivesample.MainActivity.onCreate(MainActivity.java:168)\n" +
                    "                                                                                          at android.app.Activity.performCreate(Activity.java:5720)\n" +
                    "                                                                                          at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1102)\n" +
                    "                                                                                          at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2208)\n" +
                    "                                                                                          at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2317) \n" +
                    "                                                                                          at android.app.ActivityThread.access$800(ActivityThread.java:143) \n" +
                    "                                                                                          at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1258) \n" +
                    "                                                                                          at android.os.Handler.dispatchMessage(Handler.java:102) \n" +
                    "                                                                                          at android.os.Looper.loop(Looper.java:135) \n" +
                    "                                                                                          at android.app.ActivityThread.main(ActivityThread.java:5070) \n" +
                    "                                                                                          at java.lang.reflect.Method.invoke(Native Method) \n" +
                    "                                                                                          at java.lang.reflect.Method.invoke(Method.java:372) \n" +
                    "                                                                                          at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:836) \n" +
                    "                                                                                          at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:631) ");

            Mockito.verify(listener).stacktraceFound("org.eclipse.jgit.api.errors.JGitInternalException: Exception caught during execution of fetch command\n" +
                    "    at org.eclipse.jgit.api.FetchCommand.call(FetchCommand.java:138)\n" +
                    "    at org.eclipse.jgit.api.CloneCommand.fetch(CloneCommand.java:175)\n" +
                    "    at org.eclipse.jgit.api.CloneCommand.call(CloneCommand.java:121)\n" +
                    "    at org.eclipse.egit.core.op.CloneOperation.run(CloneOperation.java:153)\n" +
                    "    at org.eclipse.egit.ui.internal.clone.AbstractGitCloneWizard.executeCloneOperation(AbstractGitCloneWizard.java:430)\n" +
                    "    at org.eclipse.egit.ui.internal.clone.AbstractGitCloneWizard.access$2(AbstractGitCloneWizard.java:424)\n" +
                    "    at org.eclipse.egit.ui.internal.clone.AbstractGitCloneWizard$6.run(AbstractGitCloneWizard.java:403)\n" +
                    "    at org.eclipse.core.internal.jobs.Worker.run(Worker.java:54)\n" +
                    "Caused by: org.eclipse.jgit.errors.TransportException: Read timed out\n" +
                    "    at org.eclipse.jgit.transport.BasePackFetchConnection.doFetch(BasePackFetchConnection.java:291)\n" +
                    "    at org.eclipse.jgit.transport.TransportHttp$SmartHttpFetchConnection.doFetch(TransportHttp.java:706)\n" +
                    "    at org.eclipse.jgit.transport.BasePackFetchConnection.fetch(BasePackFetchConnection.java:229)\n" +
                    "    at org.eclipse.jgit.transport.FetchProcess.fetchObjects(FetchProcess.java:225)\n" +
                    "    at org.eclipse.jgit.transport.FetchProcess.executeImp(FetchProcess.java:151)\n" +
                    "    at org.eclipse.jgit.transport.FetchProcess.execute(FetchProcess.java:113)\n" +
                    "    at org.eclipse.jgit.transport.Transport.fetch(Transport.java:1062)\n" +
                    "    at org.eclipse.jgit.api.FetchCommand.call(FetchCommand.java:129)\n" +
                    "    ... 7 more\n" +
                    "Caused by: java.io.InterruptedIOException: Read timed out\n" +
                    "    at org.eclipse.jgit.util.io.TimeoutInputStream.readTimedOut(TimeoutInputStream.java:140)\n" +
                    "    at org.eclipse.jgit.util.io.TimeoutInputStream.read(TimeoutInputStream.java:113)\n" +
                    "    at org.eclipse.jgit.util.IO.readFully(IO.java:223)\n" +
                    "    at org.eclipse.jgit.transport.PacketLineIn.readLength(PacketLineIn.java:186)\n" +
                    "    at org.eclipse.jgit.transport.SideBandInputStream.needDataPacket(SideBandInputStream.java:154)\n" +
                    "    at org.eclipse.jgit.transport.SideBandInputStream.read(SideBandInputStream.java:136)\n" +
                    "    at org.eclipse.jgit.transport.PackParser.fill(PackParser.java:1122)\n" +
                    "    at org.eclipse.jgit.transport.PackParser.readPackHeader(PackParser.java:826)\n" +
                    "    at org.eclipse.jgit.transport.PackParser.parse(PackParser.java:475)\n" +
                    "    at org.eclipse.jgit.storage.file.ObjectDirectoryPackParser.parse(ObjectDirectoryPackParser.java:179)\n" +
                    "    at org.eclipse.jgit.transport.PackParser.parse(PackParser.java:448)\n" +
                    "    at org.eclipse.jgit.transport.BasePackFetchConnection.receivePack(BasePackFetchConnection.java:676)\n" +
                    "    at org.eclipse.jgit.transport.BasePackFetchConnection.doFetch(BasePackFetchConnection.java:284)\n" +
                    "    ... 14 more");

            Mockito.verify(listener).stacktraceFound("                                                                    java.lang.Throwable: Explicit termination method 'close' not called\n" +
                    "                                                                        at dalvik.system.CloseGuard.open(CloseGuard.java:184)\n" +
                    "                                                                        at android.database.sqlite.SQLiteDatabase.openInner(SQLiteDatabase.java:807)\n" +
                    "                                                                        at android.database.sqlite.SQLiteDatabase.open(SQLiteDatabase.java:791)\n" +
                    "                                                                        at android.database.sqlite.SQLiteDatabase.openDatabase(SQLiteDatabase.java:694)\n" +
                    "                                                                        at android.app.ContextImpl.openOrCreateDatabase(ContextImpl.java:1071)\n" +
                    "                                                                        at android.content.ContextWrapper.openOrCreateDatabase(ContextWrapper.java:257)\n" +
                    "                                                                        at android.database.sqlite.SQLiteOpenHelper.getDatabaseLocked(SQLiteOpenHelper.java:223)\n" +
                    "                                                                        at android.database.sqlite.SQLiteOpenHelper.getWritableDatabase(SQLiteOpenHelper.java:163)\n" +
                    "                                                                        at com.google.android.gsf.gservices.GservicesProvider.computeLocalDigestAndUpdateValues(GservicesProvider.java:393)\n" +
                    "                                                                        at com.google.android.gsf.gservices.GservicesProvider.onCreate(GservicesProvider.java:173)\n" +
                    "                                                                        at android.content.ContentProvider.attachInfo(ContentProvider.java:1622)\n" +
                    "                                                                        at android.content.ContentProvider.attachInfo(ContentProvider.java:1593)\n" +
                    "                                                                        at android.app.ActivityThread.installProvider(ActivityThread.java:4843)\n" +
                    "                                                                        at android.app.ActivityThread.installContentProviders(ActivityThread.java:4438)\n" +
                    "                                                                        at android.app.ActivityThread.handleBindApplication(ActivityThread.java:4378)\n" +
                    "                                                                        at android.app.ActivityThread.access$1500(ActivityThread.java:143)\n" +
                    "                                                                        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1317)\n" +
                    "                                                                        at android.os.Handler.dispatchMessage(Handler.java:102)\n" +
                    "                                                                        at android.os.Looper.loop(Looper.java:135)\n" +
                    "                                                                        at android.app.ActivityThread.main(ActivityThread.java:5070)\n" +
                    "                                                                        at java.lang.reflect.Method.invoke(Native Method)\n" +
                    "                                                                        at java.lang.reflect.Method.invoke(Method.java:372)\n" +
                    "                                                                        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:836)\n" +
                    "                                                                        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:631)");
        } finally {
            reader.close();
        }
    }

    @Test
    public void recognizeStandardFrame() {
        checkLineType(LineType.StackFrameType, "                                                                at com.android.server.job.JobSchedulerService$JobSchedulerStub.enforceValidJobRequest(JobSchedulerService.java:632)\n");
    }

    @Test
    public void recognizeFrameWithNumbers() {
        checkLineType(LineType.StackFrameType, "                                                                                          at android.app.ActivityThread.access$800(ActivityThread.java:143) \n");
    }

    @Test
    public void recognizeCauseWithoutMessage() {
        checkLineType(LineType.CausedByTypeWithoutMessage, "                                                              Caused by: java.lang.reflect.InvocationTargetException\n");
    }

    @Test
    public void recognizeCauseWithMessageStart() {
        checkLineType(LineType.CausedByTypeWithMessage, "                                                              Caused by: android.content.res.Resources$NotFoundException: Unable to find resource ID #0x0\n");
    }

    @Test
    public void recognizeExceptionStartWithMessage() {
        checkLineType(LineType.ExceptionStartTypeWithMessage, "                                                             android.view.InflateException: Binary XML file line #34: Error inflating class android.widget.FrameLayout\n");
    }

    @Test
    public void recognizeExceptionStartWithoutMessage() {
        checkLineType(LineType.ExceptionStartTypeWithoutMessage, "                                                             android.view.InflateException     \n");
    }

    private void checkLineType(LineType expectedLineType, String line) {
        StackTraceListener listener = Mockito.mock(StackTraceListener.class);
        StackTraceMatcher matcher = new StackTraceMatcher(listener);
        Line match = matcher.recognize(line);
        assertEquals(expectedLineType, match.getType());
    }

}
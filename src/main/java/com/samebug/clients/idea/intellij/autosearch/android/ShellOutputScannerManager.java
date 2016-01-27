package com.samebug.clients.idea.intellij.autosearch.android;

import com.android.ddmlib.*;
import com.samebug.clients.api.LogScannerFactory;
import com.samebug.clients.idea.intellij.autosearch.android.exceptions.UnableToCreateReceiver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Installs a ProcessOutputScanner on Run/Debug Process standard and error outputs
 */
public class ShellOutputScannerManager {

    public ShellOutputScannerManager(@NotNull LogScannerFactory scannerFactory) {
        this.scannerFactory = scannerFactory;
    }

    public synchronized IShellOutputReceiver initReceiver(@NotNull IDevice device) throws UnableToCreateReceiver {
        Integer deviceHashCode = System.identityHashCode(device);
        IShellOutputReceiver receiver = receivers.get(deviceHashCode);
        if (receiver == null) {
            receiver = createReceiver(device, deviceHashCode);
        }
        return receiver;
    }

    private IShellOutputReceiver createReceiver(@NotNull IDevice device, Integer deviceHashCode) throws UnableToCreateReceiver {
        try {
            AndroidShellOutputScanner receiver = new AndroidShellOutputScanner(scannerFactory.createScanner());
            device.executeShellCommand("logcat -v long", receiver, 0L, TimeUnit.NANOSECONDS);
            return receiver;
        } catch (TimeoutException e) {
            throw new UnableToCreateReceiver("Unable to create receiver for device " + device.getName(), e);
        } catch (AdbCommandRejectedException e) {
            throw new UnableToCreateReceiver("Unable to create receiver for device " + device.getName(), e);
        } catch (ShellCommandUnresponsiveException e) {
            throw new UnableToCreateReceiver("Unable to create receiver for device " + device.getName(), e);
        } catch (IOException e) {
            throw new UnableToCreateReceiver("Unable to create receiver for device " + device.getName(), e);
        }
    }

    synchronized void removeReceiver(@NotNull IDevice device) {
        Integer descriptorHashCode = System.identityHashCode(device);
        AndroidShellOutputScanner receiver =  receivers.get(descriptorHashCode);
        if (receiver != null) {
            receiver.finish();
        }
        receivers.remove(descriptorHashCode);
    }

    private final LogScannerFactory scannerFactory;
    private final Map<Integer, AndroidShellOutputScanner> receivers = new HashMap<Integer, AndroidShellOutputScanner>();
}


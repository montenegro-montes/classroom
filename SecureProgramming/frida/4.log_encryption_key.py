import frida, sys

package_name = "infosecadventures.allsafe"

jscode = """
Java.perform(function() {
    console.log("[ * ] Starting implementation override...")
    var EncryptionUtil = Java.use("infosecadventures.allsafe.challenges.WeakCryptography");
    EncryptionUtil.encrypt.implementation = function(key, value) {
        console.log("[ + ] Key: " + key);
        console.log("[ + ] Value: " + value);
        return this.encrypt(key, value);
    }
});
"""


try:
    timeout = 10
    print("[ * ] Looking for app: " + package_name)
    device = frida.get_usb_device(timeout)
    print("[ * ] Launching app...")
    pid = device.spawn([package_name])
    device.resume(pid)
    session = device.attach(pid)
    exploit = session.create_script(jscode)
    print("[ + ] App launched. Loading exploit...")
    exploit.load()
    sys.stdin.read()
except frida.ServerNotRunningError:
    print("[ - ] Frida server is not running! Exiting...")
except frida.NotSupportedError:
    print("[ - ] Unable to find application. Please, install it first!")
except frida.ProcessNotFoundError:
    print("[ - ] Unable to find process. Launch the app and try again!")
except KeyboardInterrupt:
    print("\n[ - ] Interrupted. Exiting...")
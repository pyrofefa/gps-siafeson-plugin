import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(GPSSiafesonPlugin)
public class GPSSiafesonPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "GPSSiafesonPlugin"
    public let jsName = "GPSSiafeson"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = GPSSiafeson()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}

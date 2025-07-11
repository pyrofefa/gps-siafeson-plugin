// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "GpsSiafesonPlugin",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "GpsSiafesonPlugin",
            targets: ["GPSSiafesonPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "GPSSiafesonPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/GPSSiafesonPlugin"),
        .testTarget(
            name: "GPSSiafesonPluginTests",
            dependencies: ["GPSSiafesonPlugin"],
            path: "ios/Tests/GPSSiafesonPluginTests")
    ]
)
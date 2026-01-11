// swift-tools-version: 6.2
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "BridgeComponents",
    platforms: [.iOS(.v15)],
    products: [
        // Products define the executables and libraries a package produces, making them visible to other packages.
        .library(
            name: "BridgeComponents",
            targets: ["BridgeComponents"]
        ),
    ],
    dependencies: [
        .package(
            url: "https://github.com/hotwired/hotwire-native-ios.git",
            .upToNextMinor(from: "1.2.2")
        )
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .target(
            name: "BridgeComponents",
            dependencies: [
                .product(name: "HotwireNative", package: "hotwire-native-ios")
            ],
            swiftSettings: [
                .define("SWIFT_PACKAGE")
            ],
            linkerSettings: [
                .linkedFramework("UIKit")
                // Add other system frameworks here if needed
            ]
        ),
        .testTarget(
            name: "BridgeComponentsTests",
            dependencies: ["BridgeComponents"]
        )
    ]
)

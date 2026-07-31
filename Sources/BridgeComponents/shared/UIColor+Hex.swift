//
//  UIColor+Hex.swift
//  BridgeComponents
//
//  Created by Maximilian Babescu Local on 31.07.2026.
//

import SwiftUI

public extension Color {
    init(hex: String, fallback: Color = .blue) {
        let sanitized = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        
        guard Scanner(string: sanitized).scanHexInt64(&int) else {
            self = fallback
            return
        }
        
        let a, r, g, b: UInt64
        switch sanitized.count {
        case 6: // RGB (e.g. "4F46E5" or "#4F46E5")
            (a, r, g, b) = (255, (int >> 16) & 0xFF, (int >> 8) & 0xFF, int & 0xFF)
        case 8: // ARGB / RGBA
            (a, r, g, b) = ((int >> 24) & 0xFF, (int >> 16) & 0xFF, (int >> 8) & 0xFF, int & 0xFF)
        default:
            self = fallback
            return
        }

        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

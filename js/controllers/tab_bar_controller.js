import { BridgeComponent } from "@hotwired/hotwire-native-bridge";

export default class extends BridgeComponent {
  static component = "tab-bar";
  static values = {
    triggerRefresh: { type: Boolean, default: false },
    tabBarHidden: { type: Boolean, default: false },
    tabs: { type: Array, default: [] }
  };

  connect() {
    super.connect();
    this.send("connect", { tabs: this.tabsValue, tabBarHidden: this.tabBarHiddenValue, refreshUnselectedTabs: this.triggerRefreshValue }, () => {
      this.refresh();
    });
  }

  refresh() {
    Turbo.visit(window.location.href)
  }
}
import { BridgeComponent } from "@hotwired/hotwire-native-bridge";

export default class extends BridgeComponent {
  static component = "secrets-store";
  static values = {
    key: String,
    secret: String
  };

  connect() {
    super.connect();
    
    const keyValue = this.keyValue;
    const secretValue = this.secretValue;

    if (secretValue) {
      this.send("store_secret", { key: keyValue, secret: secretValue });
    }

    this.send("connect", { key: keyValue }, ({ data: { secret } }) => { 
      this.dispatch("fetched_secret", { detail: { secret } });
    });
  }

  store_secret(event) {
    const { secret } = event.detail;

    const keyValue = this.keyValue;

    this.send("store_secret", { key: keyValue, secret: secret });
  }
}
interface Panels {
  [names: string]: () => Promise<void>;
}

export default class PanelManager {
  private readonly panels: Panels;

  private currentPanel: string | null;

  constructor() {
    this.panels = {};
    this.currentPanel = null;
  }

  registerPanel(name: string, callback: () => Promise<void>) {
    if (
      Object.prototype.hasOwnProperty.call(this.panels, name) ||
      document.getElementById(name) === null
    )
      return;
    this.panels[name] = callback;
  }

  async switchToPanel(name: string) {
    if (
      this.currentPanel !== null &&
      (this.currentPanel === name ||
        !Object.prototype.hasOwnProperty.call(this.panels, name))
    )
      return;
    if (this.currentPanel !== null) {
      document.getElementById(this.currentPanel)?.classList.add("hidden");
    }
    this.currentPanel = name;
    if (Object.prototype.hasOwnProperty.call(this.panels, name)) {
      await this.panels[name]();
    }
    document.getElementById(name)?.classList.remove("hidden");
  }
}

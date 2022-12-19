import { appWindow } from "@tauri-apps/api/window";
import { invoke } from "@tauri-apps/api/tauri";

const titleBarClose: HTMLElement | null =
  document.getElementById("titlebar-close");
const titleBarMaximize: HTMLElement | null =
  document.getElementById("titlebar-maximize");
const titleBarMinimize: HTMLElement | null =
  document.getElementById("titlebar-minimize");
const musicProgress: HTMLElement | null =
  document.getElementById("music-progress");
const musicCover = document.getElementById("music-cover")! as HTMLImageElement;
const musicTitle = document.getElementById(
  "music-title"
)! as HTMLParagraphElement;
const musicSubtitle = document.getElementById(
  "music-subtitle"
)! as HTMLParagraphElement;
const playButton: HTMLElement | null = document.getElementById("play-button");
const playButtonResume: HTMLElement | null =
  document.getElementById("play-button-resume");
const playButtonPause: HTMLElement | null =
  document.getElementById("play-button-pause");
let musicInfosInterval: number | null = null;

titleBarClose?.addEventListener("click", () => appWindow.close());
titleBarMaximize?.addEventListener("click", () => appWindow.toggleMaximize());
titleBarMinimize?.addEventListener("click", () => appWindow.minimize());

const soundInterval = async () => {
  const currentInfos: any = await invoke("sound_infos", {});
  if (
    currentInfos !== null &&
    currentInfos !== undefined &&
    musicProgress !== null
  ) {
    musicProgress.style.width = `${
      (currentInfos.progress.secs / currentInfos.duration.secs) * 100
    }%`;
    initSoundInfos(currentInfos);
  }
};

playButton?.addEventListener("click", async () => {
  if (
    playButtonResume !== null &&
    playButtonResume.classList.contains("hidden")
  ) {
    await invoke("pause", {});
    if (playButtonPause !== null) {
      playButtonPause.classList.add("hidden");
    }
    playButtonResume.classList.remove("hidden");
    if (musicInfosInterval !== null) {
      clearInterval(musicInfosInterval);
    }
  } else {
    await invoke("resume", {});
    if (playButtonResume !== null) {
      playButtonResume.classList.add("hidden");
    }
    if (playButtonPause !== null) {
      playButtonPause.classList.remove("hidden");
    }
    musicInfosInterval = setInterval(() => soundInterval(), 1000);
  }
});

const initSoundInfos = (res: any) => {
  if (musicCover !== null) {
    musicCover.src = `data:image/png;base64,${res.file_infos.cover}`;
  }
  if (musicTitle !== null) {
    musicTitle.textContent = res.file_infos.title;
  }
  if (musicSubtitle !== null) {
    musicSubtitle.textContent = `${res.file_infos.artist} - ${res.file_infos.album}`;
  }
};

musicInfosInterval = setInterval(() => soundInterval(), 1000);

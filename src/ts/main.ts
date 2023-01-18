import { appWindow } from "@tauri-apps/api/window";
import { invoke, convertFileSrc } from "@tauri-apps/api/tauri";
import PanelManager from "./panel_manager";
import formatDuration from "./utils";
import { Album, ListAlbums, MusicInfos, Track } from "./jsons";

const panelManager = new PanelManager();
const titleBarClose: HTMLElement | null =
  document.getElementById("titlebar-close");
const titleBarMaximize: HTMLElement | null =
  document.getElementById("titlebar-maximize");
const titleBarMinimize: HTMLElement | null =
  document.getElementById("titlebar-minimize");
const musicProgress: HTMLElement | null =
  document.getElementById("music-progress");
const musicCover = document.getElementById("music-cover")! as HTMLImageElement;
const noMusicCover: HTMLElement = document.getElementById("no-music-cover")!;
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
const panelAlbumList: HTMLElement =
  document.getElementById("panel-album-list")!;
const panelTracksListContainer: HTMLElement = document.getElementById(
  "panel-tracks-list-container"
)!;
const panelTracksListAlbumName: HTMLElement = document.getElementById(
  "panel-tracks-list-album-name"
)!;
const panelTracksListAlbumCover = document.getElementById(
  "panel-tracks-list-album-cover"
)! as HTMLImageElement;
const panelTracksListAlbumYear: HTMLElement = document.getElementById(
  "panel-tracks-list-album-year"
)!;
const panelTracksListAlbumArtist: HTMLElement = document.getElementById(
  "panel-tracks-list-album-artist"
)!;
const panelTracksListCloseButton: HTMLElement | null = document.getElementById(
  "panel-tracks-list-close-button"
);
let musicInfosInterval: number | null = null;
let currentAlbumSelectedUUID: string | null = null;

titleBarClose?.addEventListener("click", () => appWindow.close());
titleBarMaximize?.addEventListener("click", () => appWindow.toggleMaximize());
titleBarMinimize?.addEventListener("click", () => appWindow.minimize());
panelTracksListCloseButton?.addEventListener("click", async () => {
  await panelManager.switchToPanel("panel-album");
});
const initSoundInfos = (res: MusicInfos) => {
  if (res.file_infos.length === 2) {
    if (
      musicCover !== null &&
      res.file_infos[1] !== null &&
      res.file_infos[1].cover === 1
    ) {
      musicCover.src = convertFileSrc(
        `${res.cover_path}/${res.file_infos[1].uuid}.png`
      );
      noMusicCover?.classList.add("hidden");
      musicCover.classList.remove("hidden");
    } else {
      musicCover.classList.add("hidden");
      noMusicCover?.classList.remove("hidden");
    }

    if (musicSubtitle !== null && res.file_infos[1] !== null) {
      musicSubtitle.textContent = `${res.file_infos[1].artist} - ${res.file_infos[1].name}`;
    }
  } else {
    musicCover.classList.add("hidden");
    noMusicCover?.classList.remove("hidden");
  }

  if (musicTitle !== null) {
    musicTitle.textContent = res.file_infos[0].title;
  }
};

const soundInterval = async () => {
  const currentInfos: MusicInfos = await invoke("sound_infos", {});
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

musicInfosInterval = setInterval(() => soundInterval(), 1000);

panelManager.registerPanel("panel-album", async () => {
  const fetchAlbums: ListAlbums = await invoke("list_albums", {});

  let htmlToAdd = "";

  // eslint-disable-next-line no-restricted-syntax
  for (const album of fetchAlbums.list) {
    const addHiddenForArtist = album.artist === null ? " hidden" : "";
    const addCover =
      album.cover === 0
        ? ""
        : `<img class="w-44 h-44 rounded-lg pt-2" src="${convertFileSrc(
            `${fetchAlbums.pictures_path}/${album.uuid}.png`
          )}">`;
    const addHiddenForDiv = album.cover === 1 ? " hidden" : "";

    htmlToAdd += `<div class="bg-dark-celeria w-[15rem] hover:w-[30rem] duration-500 ease-in-out h-80 p-8 rounded-sm group cursor-pointer m-4 album-element" uuid="${album.uuid}">
        ${addCover}
        <div class="w-44 h-44 rounded-lg bg-white-3${addHiddenForDiv}"></div>
        <div class="w-[10rem] group-hover:w-[25rem] duration-500 ease-in-out h-[7rem]">
          <h2 class="text-white-1 text-mukta text-2xl break-properly font-bold pt-2">${album.name}</h2>
          <h3 class="pt-1 text-white-1 text-mukta break-properly text-xl${addHiddenForArtist}">${album.artist}</h3>
        </div>
    </div>`;
    panelAlbumList.innerHTML = htmlToAdd;

    for (const fetchedElement of document.querySelectorAll(".album-element")) {
      const element: HTMLElement = fetchedElement as HTMLElement;

      element.onclick = async () => {
        currentAlbumSelectedUUID = element.getAttribute("uuid");
        await panelManager.switchToPanel("panel-tracks-list");
      };
    }
  }
});

panelManager.registerPanel("panel-tracks-list", async () => {
  const albumInfos: [Album, [Track], string] = await invoke("album_infos", {
    uuid: currentAlbumSelectedUUID,
  });

  panelTracksListAlbumCover.src = convertFileSrc(
    `${albumInfos[2]}/${albumInfos[0].uuid}.png`
  );
  panelTracksListAlbumName.innerText = albumInfos[0].name;
  if (albumInfos[0].artist !== null) {
    panelTracksListAlbumArtist.innerText = albumInfos[0].artist;
  }
  if (albumInfos[0].year !== null) {
    panelTracksListAlbumYear.innerText = albumInfos[0].year.toString();
  }

  let htmlToAdd = "";
  // eslint-disable-next-line no-restricted-syntax
  for (const [i, track] of albumInfos[1].entries()) {
    htmlToAdd += `<div class="flex flex-row justify-between hover:bg-dark-celeria cursor-pointer p-3 rounded-lg track-element" uuid="${
      track.uuid
    }">
        <div class="flex items-center flex-row space-x-6">
            <span class="text-mukta text-xl text-white-1">${i + 1}</span>
            <p class="text-mukta text-lg text-white-1">${track.title}</p>
        </div>
        <div class="flex items-center flex-row space-x-6 pr-2">
            <p class="text-mukta text-lg text-white-1 opacity-80">${formatDuration(
              track.duration
            )}</p>
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6 text-white-1">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0zM12.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0zM18.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0z" />
            </svg>
        </div>
    </div>`;
  }
  panelTracksListContainer.innerHTML = htmlToAdd;
  // eslint-disable-next-line no-restricted-syntax
  for (const fetchedElement of document.querySelectorAll(".track-element")) {
    const element: HTMLElement = fetchedElement as HTMLElement;

    element.onclick = () => {
      invoke("play_sound", {
        trackUuid: element.getAttribute("uuid"),
      })
        .then()
        .catch();
    };
  }
});
window.onload = async () => {
  await panelManager.switchToPanel("panel-album");
};

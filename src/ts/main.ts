// eslint-disable-next-line import/no-unresolved
import { appWindow } from "@tauri-apps/api/window";
// eslint-disable-next-line import/no-unresolved
import { invoke, convertFileSrc } from "@tauri-apps/api/tauri";
import PanelManager from "./panel_manager";
import formatDuration from "./utils";
import { Album, Infos, MusicInfos, Track } from "./jsons";
import MusicProgress from "./custom_elements/music_progress";

const panelManager = new PanelManager();
const titleBarClose: HTMLElement | null =
  document.getElementById("titlebar-close");
const titleBarMaximize: HTMLElement | null =
  document.getElementById("titlebar-maximize");
const titleBarMinimize: HTMLElement | null =
  document.getElementById("titlebar-minimize");
const musicProgress: MusicProgress = document.getElementById(
  "music-progress"
)! as MusicProgress;
const musicCover = document.getElementById("music-cover")! as HTMLImageElement;
const noMusicCover: HTMLElement = document.getElementById("no-music-cover")!;
const musicTitle = document.getElementById(
  "music-title"
)! as HTMLParagraphElement;
const musicSubtitle = document.getElementById(
  "music-subtitle"
)! as HTMLParagraphElement;
const soundProgressBar: HTMLInputElement = document.getElementById(
  "sound-progressbar"
)! as HTMLInputElement;
const playButton: HTMLElement | null = document.getElementById("play-button");
const nextButton: HTMLElement | null = document.getElementById("next-button");
const previousButton: HTMLElement | null =
  document.getElementById("previous-button");
const playButtonResume: HTMLElement =
  document.getElementById("play-button-resume")!;
const playButtonPause: HTMLElement =
  document.getElementById("play-button-pause")!;
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
const leftMenuItemAlbums: HTMLElement = document.getElementById(
  "left-menu-item-albums"
)!;
const leftMenuSeparatorAlbums: HTMLElement = document.getElementById(
  "left-menu-separator-albums"
)!;
const leftMenuItemTracks: HTMLElement = document.getElementById(
  "left-menu-item-tracks"
)!;
const leftMenuSeparatorTracks: HTMLElement = document.getElementById(
  "left-menu-separator-tracks"
)!;
const leftMenuItemPlaylists: HTMLElement = document.getElementById(
  "left-menu-item-playlists"
)!;
const leftMenuSeparatorPlaylists: HTMLElement = document.getElementById(
  "left-menu-separator-playlists"
)!;
const leftMenuItemDownloader: HTMLElement = document.getElementById(
  "left-menu-item-downloader"
)!;

let leftMenuPreviousSelectedItem: HTMLElement = leftMenuItemAlbums;
let leftMenuPreviousSelectedSeparator: HTMLElement | null =
  leftMenuSeparatorAlbums;
let currentAlbumSelectedUUID: string | null = null;
let currentPlayerSoundUUID: string | null = null;
const infos: Infos = await invoke("infos", {});

titleBarClose?.addEventListener("click", () => appWindow.close());
titleBarMaximize?.addEventListener("click", () => appWindow.toggleMaximize());
titleBarMinimize?.addEventListener("click", () => appWindow.minimize());

const leftMenuSwitch = (
  element: HTMLElement,
  separator: HTMLElement | null
) => {
  if (leftMenuPreviousSelectedSeparator !== null) {
    leftMenuPreviousSelectedSeparator.classList.remove("hidden");
  }
  if (separator !== null) {
    separator.classList.add("hidden");
  }

  leftMenuPreviousSelectedItem.classList.remove(
    "left-menu-selection-selected",
    "left-menu-selection",
    "mb-2"
  );
  element.classList.add(
    "left-menu-selection-selected",
    "left-menu-selection",
    "mb-2"
  );

  leftMenuPreviousSelectedItem = element;
  leftMenuPreviousSelectedSeparator = separator;
};

leftMenuItemTracks?.addEventListener("click", () => {
  if (leftMenuPreviousSelectedItem === leftMenuItemTracks) return;

  panelManager.switchToPanel("panel-in-dev").then();
  leftMenuSwitch(leftMenuItemTracks, leftMenuSeparatorTracks);
});

leftMenuItemAlbums?.addEventListener("click", () => {
  if (leftMenuPreviousSelectedItem === leftMenuItemAlbums) return;

  panelManager.switchToPanel("panel-album").then();
  leftMenuSwitch(leftMenuItemAlbums, leftMenuSeparatorAlbums);
});
leftMenuItemPlaylists?.addEventListener("click", () => {
  if (leftMenuPreviousSelectedItem === leftMenuItemPlaylists) return;

  panelManager.switchToPanel("panel-in-dev").then();
  leftMenuSwitch(leftMenuItemPlaylists, leftMenuSeparatorPlaylists);
});
leftMenuItemDownloader?.addEventListener("click", () => {
  if (leftMenuPreviousSelectedItem === leftMenuItemDownloader) return;

  panelManager.switchToPanel("panel-in-dev").then();
  leftMenuSwitch(leftMenuItemDownloader, null);
});

panelTracksListCloseButton?.addEventListener("click", async () => {
  await panelManager.switchToPanel("panel-album");
});

musicProgress?.addEventListener(
  "music-progress-change",
  async (event: Event) => {
    if ((<CustomEvent>event).detail === null) return;

    await invoke("set_progress", {
      time: parseInt((<CustomEvent>event).detail, 10),
    });
  }
);

const initSoundInfos = (res: MusicInfos) => {
  if (res.file_infos.length === 2) {
    if (res.file_infos[0].uuid !== currentPlayerSoundUUID) {
      if (
        musicCover !== null &&
        res.file_infos[1] !== null &&
        res.file_infos[1].cover === 1
      ) {
        musicCover.src = convertFileSrc(
          `${infos.covers_path}/${res.file_infos[1].uuid}.png`
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
    }
    currentPlayerSoundUUID = res.file_infos[0].uuid;
  } else {
    currentPlayerSoundUUID = null;
    musicCover.classList.add("hidden");
    noMusicCover?.classList.remove("hidden");
  }

  if (musicTitle !== null) {
    musicTitle.textContent = res.file_infos[0].title;
  }
};

const soundInterval = async () => {
  const currentInfos: MusicInfos = await invoke("sound_infos", {});
  if (currentInfos !== null && currentInfos !== undefined) {
    if (currentInfos.paused) {
      playButtonPause.classList.add("hidden");
      playButtonResume.classList.remove("hidden");
    } else {
      playButtonResume.classList.add("hidden");
      playButtonPause.classList.remove("hidden");
    }
    musicProgress.setMaxValue(currentInfos.duration.secs);
    musicProgress.classList.remove("hidden");
    musicProgress.setCurrentProgress(currentInfos.progress.secs);

    initSoundInfos(currentInfos);
  } else {
    musicProgress.classList.add("hidden");
    musicProgress.setCurrentProgress(0);
    musicCover.classList.add("hidden");
    noMusicCover?.classList.remove("hidden");
    musicTitle.textContent = "";
    musicSubtitle.textContent = "";
  }
};

nextButton?.addEventListener("click", async () => {
  await invoke("next", {});
});

previousButton?.addEventListener("click", async () => {
  await invoke("previous", {});
});

playButton?.addEventListener("click", async () => {
  if (playButtonResume.classList.contains("hidden")) {
    const result: boolean = await invoke("pause", {});
    if (result) {
      playButtonPause.classList.add("hidden");
      playButtonResume.classList.remove("hidden");
    }
  } else {
    const result: boolean = await invoke("resume", {});
    if (result) {
      playButtonResume.classList.add("hidden");
      playButtonPause.classList.remove("hidden");
    }
  }
});

soundProgressBar?.addEventListener("input", async (event: Event) => {
  if (event.target === null) return;
  const target = event.target as HTMLInputElement;

  if (parseInt(target.value, 10) < 101 && parseInt(target.value, 10) >= 0) {
    await invoke("set_volume", { volume: parseInt(target.value, 10) });
  }
});

setInterval(() => soundInterval(), 500);

panelManager.registerPanel("panel-album", async () => {
  const fetchAlbums: [Album] = await invoke("list_albums", {});

  let htmlToAdd = "";

  // eslint-disable-next-line no-restricted-syntax
  for (const album of fetchAlbums) {
    const addHiddenForArtist = album.artist === null ? " hidden" : "";
    const addCover =
      album.cover === 0
        ? ""
        : `<img class="w-44 h-44 rounded-lg pt-2" src="${convertFileSrc(
            `${infos.covers_path}/${album.uuid}.png`
          )}">`;
    const addHiddenForDiv = album.cover === 1 ? " hidden" : "";

    htmlToAdd += `<div class="bg-dark-celeria w-[15rem] hover:w-[32rem] duration-500 ease-in-out h-80 p-8 rounded-sm group cursor-pointer m-4 album-element" uuid="${album.uuid}">
        ${addCover}
        <div class="w-44 h-44 rounded-lg bg-white-3${addHiddenForDiv}"></div>
        <div class="w-[10rem] group-hover:w-[25rem] duration-500 ease-in-out h-[7rem]">
          <h2 class="text-white-1 text-mukta text-2xl break-properly font-bold pt-2">${album.name}</h2>
          <h3 class="pt-1 text-white-1 text-mukta break-properly text-xl${addHiddenForArtist}">${album.artist}</h3>
        </div>
    </div>`;
    panelAlbumList.innerHTML = htmlToAdd;

    // eslint-disable-next-line no-restricted-syntax
    for (const fetchedElement of document.querySelectorAll(".album-element")) {
      const element: HTMLElement = fetchedElement as HTMLElement;

      // eslint-disable-next-line no-loop-func
      element.onclick = async () => {
        currentAlbumSelectedUUID = element.getAttribute("uuid");
        await panelManager.switchToPanel("panel-tracks-list");
      };
    }
  }
});

panelManager.registerPanel("panel-tracks-list", async () => {
  const albumInfos: [Album, [Track]] = await invoke("album_infos", {
    uuid: currentAlbumSelectedUUID,
  });

  panelTracksListAlbumCover.src = convertFileSrc(
    `${infos.covers_path}/${albumInfos[0].uuid}.png`
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
        <div class="flex items-center flex-row space-x-4">
            <span class="text-mukta text-md text-white-1 w-6 flex justify-end">${
              i + 1
            }</span>
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
panelManager.registerPanel("panel-in-dev", null);

panelManager.switchToPanel("panel-album").then();

const formatDuration = (duration: number) => {
  const hours = Math.floor(duration / 3600);
  const minutes = Math.floor((duration - hours * 3600) / 60);
  const seconds = duration - hours * 3600 - minutes * 60;
  let formattedTime = "";

  if (hours !== 0) {
    if (hours < 10) {
      formattedTime += `0${hours}:`;
    } else {
      formattedTime += `${hours}:`;
    }
  }
  if (minutes < 10) {
    formattedTime += `0${minutes}:`;
  } else {
    formattedTime += `${minutes}:`;
  }
  if (seconds < 10) {
    formattedTime += `0${seconds}`;
  } else {
    formattedTime += `${seconds}`;
  }

  if (hours === 0) {
    return formattedTime;
  }
  return formattedTime;
};
export default formatDuration;

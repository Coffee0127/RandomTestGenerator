export const isJsonFormat = (jsonString: string) => {
  try {
    JSON.parse(jsonString);
  } catch (e) {
    return false;
  }
  return true;
};

export const serializeObject = (obj: {}) => {
  let arr = [];
  for (let key in obj) {
    if (obj.hasOwnProperty(key)) {
      arr.push(key + '=' + obj[key]);
    }
  };
  return arr.join('&');
}

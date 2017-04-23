export const isJsonFormat = (jsonString: string) => {
  try {
    JSON.parse(jsonString);
  } catch (e) {
    return false;
  }
  return true;
};

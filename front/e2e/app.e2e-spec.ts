import { RandomTestGeneratorPage } from './app.po';

describe('random-test-generator App', () => {
  let page: RandomTestGeneratorPage;

  beforeEach(() => {
    page = new RandomTestGeneratorPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});

import { TestBed, inject } from '@angular/core/testing';
import { TestGeneratorService } from './test-generator.service';

describe('TestGeneratorService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TestGeneratorService]
    });
  });

  it('should ...', inject([TestGeneratorService], (service: TestGeneratorService) => {
    expect(service).toBeTruthy();
  }));
});

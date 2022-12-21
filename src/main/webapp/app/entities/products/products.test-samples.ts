import { IProducts, NewProducts } from './products.model';

export const sampleWithRequiredData: IProducts = {
  id: 79224,
  articalName: 'Switchable Alaska',
  articalPrice: 23782,
};

export const sampleWithPartialData: IProducts = {
  id: 28734,
  articalName: 'deposit',
  articalPrice: 98747,
};

export const sampleWithFullData: IProducts = {
  id: 9684,
  articalName: 'orchid payment',
  articalPrice: 54991,
};

export const sampleWithNewData: NewProducts = {
  articalName: 'parsing Infrastructure',
  articalPrice: 98000,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

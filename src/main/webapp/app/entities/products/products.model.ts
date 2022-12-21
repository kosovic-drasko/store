export interface IProducts {
  id: number;
  articalName?: string | null;
  articalPrice?: number | null;
}

export type NewProducts = Omit<IProducts, 'id'> & { id: null };

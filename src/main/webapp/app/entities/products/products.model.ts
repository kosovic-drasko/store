export interface IProducts {
  id: number;
  articalName?: string | null;
  articalPrice?: number | null;
}
export class Products implements IProducts {
  constructor(public id: number, public articalName?: string | null, public articalPrice?: number | null) {}
}
// export type NewProducts = Omit<IProducts, 'id'> & { id: null };

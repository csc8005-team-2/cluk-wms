import {StockItem} from './stock-item';

export interface OrderEntry {
  orderId: number;
  dateTime: string;
  address: string;
  contents: StockItem[];
}

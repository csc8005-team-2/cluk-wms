import {MealPrice} from './meal-price';

export interface MealOrder {
  mealItem: MealPrice;
  quantity: number;
}

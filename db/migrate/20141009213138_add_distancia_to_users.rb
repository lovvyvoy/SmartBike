class AddDistanciaToUsers < ActiveRecord::Migration
  def change
    add_column :users, :distancia, :integer
    add_column :users, :tiempo, :integer
  end
end

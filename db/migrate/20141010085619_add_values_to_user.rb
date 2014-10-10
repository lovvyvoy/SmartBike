class AddValuesToUser < ActiveRecord::Migration
  def change
    add_column :users, :peso, :float
    add_column :users, :sexo, :boolean
    add_column :users, :edad, :integer
  end
end

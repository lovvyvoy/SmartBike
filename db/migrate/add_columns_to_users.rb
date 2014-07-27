class AddColumnsToUsers < ActiveRecord::Migration
	def change
		add_column :users, :name, :string
		add_column :users, :lastname, :string
		add_column :users, :disp, :integer
		add_column :users, :height, :decimal
		add_column :users, :weight, :decimal
		add_column :users, :age, :integer
	end
end
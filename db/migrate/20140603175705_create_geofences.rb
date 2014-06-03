class CreateGeofences < ActiveRecord::Migration
  def change
    create_table :geofences do |t|
      t.integer :user_id
      t.decimal :x_act
      t.decimal :y_act
      t.decimal :x_al
      t.decimal :y_al
      t.boolean :alarm

      t.timestamps
    end
    add_index :geofences, :user_id, unique: true
  end
end

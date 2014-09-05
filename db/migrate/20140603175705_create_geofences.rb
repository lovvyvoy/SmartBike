class CreateGeofences < ActiveRecord::Migration
  def change
    create_table :geofences do |t|
      t.integer :user_id
      t.float :x_act
      t.float :y_act
      t.float :x_al
      t.float :y_al
      t.boolean :alarm

      t.timestamps
    end
    add_index :geofences, :user_id, unique: true
  end
end

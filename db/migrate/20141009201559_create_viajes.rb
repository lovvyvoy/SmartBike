class CreateViajes < ActiveRecord::Migration
  def change
    create_table :viajes do |t|
      t.integer :id_viaje
      t.integer :user_id
      t.float :distancia
      t.integer :tiempo
      t.date :fecha

      t.timestamps
    end
  end
end

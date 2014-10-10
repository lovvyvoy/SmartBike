class CreateLogros < ActiveRecord::Migration
  def change
    create_table :logros do |t|
      t.integer :id_logro
      t.float :meta
      t.float :int_multiuso
      t.boolean :logrado
      t.string :nombre
      t.date :date_inicial
      t.date :date_final

      t.timestamps
    end
  end
end

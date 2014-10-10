class AddTerminadoToViajes < ActiveRecord::Migration
  def change
    add_column :viajes, :terminado, :boolean
  end
end

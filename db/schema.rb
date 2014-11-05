  # encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20141024182224) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "geofences", force: true do |t|
    t.integer  "user_id"
    t.decimal  "x_act"
    t.decimal  "y_act"
    t.decimal  "x_al"
    t.decimal  "y_al"
    t.boolean  "alarm"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.decimal  "distancia_ultimo_punto", default: 0.0
  end

  add_index "geofences", ["user_id"], name: "index_geofences_on_user_id", unique: true, using: :btree

  create_table "logros", force: true do |t|
    t.integer  "id_logro"
    t.float    "meta"
    t.float    "int_multiuso", default: 0.0
    t.boolean  "logrado",      default: false
    t.string   "nombre"
    t.date     "date_inicial"
    t.date     "date_final"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "user_id"
  end

  create_table "maps", force: true do |t|
    t.float    "latitude"
    t.float    "longitude"
    t.string   "address"
    t.text     "descripcion"
    t.string   "title"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "users", force: true do |t|
    t.string   "email",                  default: "",  null: false
    t.string   "encrypted_password",     default: "",  null: false
    t.string   "reset_password_token"
    t.datetime "reset_password_sent_at"
    t.datetime "remember_created_at"
    t.integer  "sign_in_count",          default: 0,   null: false
    t.datetime "current_sign_in_at"
    t.datetime "last_sign_in_at"
    t.string   "current_sign_in_ip"
    t.string   "last_sign_in_ip"
    t.string   "address"
    t.string   "location"
    t.float    "distancia",              default: 0.0
    t.integer  "tiempo",                 default: 0
    t.integer  "calorias",               default: 0
    t.float    "co2",                    default: 0.0
    t.float    "peso"
    t.boolean  "sexo"
    t.integer  "edad"
    t.string   "provider"
    t.string   "uid"
    t.string   "name"
    t.string   "image"
  end

  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree
  add_index "users", ["reset_password_token"], name: "index_users_on_reset_password_token", unique: true, using: :btree

  create_table "viajes", force: true do |t|
    t.integer  "id_viaje"
    t.integer  "user_id"
    t.float    "distancia",  default: 0.0
    t.integer  "tiempo",     default: 0
    t.date     "fecha"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "terminado"
    t.integer  "calorias",   default: 0
    t.float    "co2",        default: 0.0
  end

end

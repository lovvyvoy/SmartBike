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

ActiveRecord::Schema.define(version: 20141009214330) do

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
  end

  add_index "geofences", ["user_id"], name: "index_geofences_on_user_id", unique: true, using: :btree

  create_table "logro", id: false, force: true do |t|
    t.integer "id_logro",                null: false
    t.string  "nombre",       limit: 40
    t.integer "logrado"
    t.integer "int_multiuso"
    t.date    "date_inicial"
    t.date    "date_final"
  end

  create_table "logro_persona", id: false, force: true do |t|
    t.integer "id_logro"
    t.integer "id_persona"
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
    t.string   "email",                  default: "", null: false
    t.string   "encrypted_password",     default: "", null: false
    t.string   "reset_password_token"
    t.datetime "reset_password_sent_at"
    t.datetime "remember_created_at"
    t.integer  "sign_in_count",          default: 0,  null: false
    t.datetime "current_sign_in_at"
    t.datetime "last_sign_in_at"
    t.string   "current_sign_in_ip"
    t.string   "last_sign_in_ip"
    t.string   "address"
    t.string   "location"
    t.integer  "distancia"
    t.integer  "tiempo"
  end

  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree
  add_index "users", ["reset_password_token"], name: "index_users_on_reset_password_token", unique: true, using: :btree

  create_table "viajes", force: true do |t|
    t.integer  "id_viaje"
    t.integer  "user_id"
    t.float    "distancia"
    t.integer  "tiempo"
    t.date     "fecha"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.boolean  "terminado"
  end

end

class GeofencesController < ApplicationController
  before_action :set_geofence, only: [:show, :edit, :update, :destroy]

  # GET /geofences
  # GET /geofences.json
  def index
    @geofences = Geofence.all
    @hash = Gmaps4rails.build_markers(@geofences) do |geofence, marker|
        marker.lat geofence.y_act
        marker.lng geofence.x_act
    end
  end

  # GET /geofences/1
  # GET /geofences/1.json
  def show
  end

  # GET /geofences/new
  def new
    @geofence = Geofence.new
  end

  # GET /geofences/1/edit
  def edit
  end

  # POST /geofences
  # POST /geofences.json
  def create
    @geofence = Geofence.new(geofence_params)

    respond_to do |format|
      if @geofence.save
        format.html { redirect_to @geofence, notice: 'Geofence was successfully created.' }
        format.json { render :show, status: :created, location: @geofence }
      else
        format.html { render :new }
        format.json { render json: @geofence.errors, status: :unprocessable_entity }
      end
    end
  end

  # PATCH/PUT /geofences/1
  # PATCH/PUT /geofences/1.json
  def update
    respond_to do |format|
      if @geofence.update(geofence_params)
        format.html { redirect_to @geofence, notice: 'Geofence was successfully updated.' }
        format.json { render :show, status: :ok, location: @geofence }
      else
        format.html { render :edit }
        format.json { render json: @geofence.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /geofences/1
  # DELETE /geofences/1.json
  def destroy
    @geofence.destroy
    respond_to do |format|
      format.html { redirect_to geofences_url, notice: 'Geofence was successfully destroyed.' }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_geofence
      @geofence = Geofence.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def geofence_params
      params.require(:geofence).permit(:user_id, :x_act, :y_act, :x_al, :y_al, :alarm)
    end
end

# Get all restaurants in Melbourne
from googleplaces import GooglePlaces, types
from geopy.geocoders import Nominatim

API_KEY = open('mykey').read()
googleSearch = GooglePlaces(API_KEY)

def getQueriesOfRestaurants(coordinates):
  results = googleSearch.nearby_search(lat_lng=coordinates,
                                       radius=20000,
                                       types=[types.TYPE_RESTAURANT] or [types.TYPE_FOOD])
  for restaurant in results.places:
    print (restaurant.place_id, restaurant.name, restaurant.geo_location)

# melbourneCentral = {'lat': -37.814, 'lng': 144.96332}

SUBURNS = ['Carlton, Melbourne', 'Docklands', 'Parkville', 'Port Melbourne', 'Southbank']
geolocator = Nominatim(user_agent="SUBURNS_GEO")
for suburn in SUBURNS:
  location = geolocator.geocode(suburn)
  locationCoordinates = {'lat': location.latitude, 'lng': location.longitude}
  getQueriesOfRestaurants(locationCoordinates)

# Download images from Google image by restaurant name

# Get the coordinates by restaurant name

# Save into a csv file


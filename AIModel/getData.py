from geopy.geocoders import Nominatim
from google_images_download import google_images_download
from zomathon import ZomatoAPI
import pandas as pd 

ZOMATO_KEY = open('api_key/zomato_key').read()
zomato = ZomatoAPI(ZOMATO_KEY)

def getAllRestaurants():
  """
  Get all restaurants in Melbourne:
  Returns:
    - Restaurant ID (Zomato)
    - Restaurant Name
    - Coordinates
  """
  restaurantDict = {}
  SUBURNS = ['Melbourne Central', 'Docklands Melbourne', 'University of Melbourne', 'Port Melbourne', 'Southbank Melbourne', 'South Yarra Melbourne'] #'East Melbourne', 'North Melbourne', 'West Melbourne', 
  geolocator = Nominatim(user_agent="SUBURNS_GEO")
  for suburn in SUBURNS:
    location = geolocator.geocode(suburn)
    locationCoordinates = {'lat': location.latitude, 'lng': location.longitude}

    results =  zomato.search(lat=locationCoordinates['lat'], lon=locationCoordinates['lng'], radius=20000, sort='real_distance', count=100)
    restaurants = results['restaurants']
    for restaurant in restaurants:
      resId = restaurant['restaurant']['id']
      resName = restaurant['restaurant']['name']
      resLatitude = restaurant['restaurant']['location']['latitude']
      resLongitude = restaurant['restaurant']['location']['longitude']
      resAddress = restaurant['restaurant']['location']['address']
      if resId not in restaurantDict:
        restaurantDict[resId] = [resName, resLatitude, resLongitude, resAddress]
  pdRestaurant = pd.DataFrame.from_dict(restaurantDict, orient='index',columns=['Restaurant Name', 'Latitude', 'Longitude', 'Address'])
  pdRestaurant.to_csv('../data/training/restaurants.csv')

def getGoogleImages():
  """
  Download images from Google image by restaurant name
  """
  googleImages = google_images_download.googleimagesdownload()
  pdRestaurant = pd.read_csv('../data/training/restaurants.csv', index_col=0, encoding='ISO-8859-1')
  # pdRestaurant['Latitude'] = pd.to_numeric(pdRestaurant['Latitude'])
  # pdRestaurant['Longitude'] = pd.to_numeric(pdRestaurant['Longitude'])
  for index in pdRestaurant.index:
    restaurant = pdRestaurant.loc[index, 'Restaurant Name']
    address = pdRestaurant.loc[index, 'Address']
    keysearch = restaurant + " " + address + " facade"
    keysearch = keysearch.replace(',','')
    query = {"keywords": keysearch, "limit": 6, \
             "output_directory": "../data/training/images", "image_directory": str(index), \
             "prefix": str(index) + "_", "format": "jpg"}
    try:
      googleImages.download(query)
    except:
      continue
  
if __name__ == "__main__":  
  # getAllRestaurants()
  getGoogleImages()
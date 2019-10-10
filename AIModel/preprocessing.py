import os
from keras.preprocessing.image import ImageDataGenerator, load_img, img_to_array

img_height = 224
img_width = 224

datagen = ImageDataGenerator(rotation_range=40,
		width_shift_range=0.2,
		height_shift_range=0.2,
		zoom_range=0.2)

img_path = 'test.jpg'
img = load_img(img_path, target_size=(img_height, img_width))
#img = load_img(img_path)
x = img_to_array(img)
x = x.reshape((1,) + x.shape)

#datagen.fit(x)

#print x.shape
if not os.path.exists('preview'):	
	os.mkdir('preview')

i = 0
for batch in datagen.flow(x, batch_size=1, save_to_dir='preview', save_prefix='hehe', save_format='jpeg'):
	i += 1
	if i > 2:
		break
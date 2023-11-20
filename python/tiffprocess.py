# from PIL import Image
# from PIL.TiffTags import TAGS

# with Image.open('prueba01.tif') as img:
#     meta_dict = {TAGS[key] : img.tag[key] for key in img.tag.iterkeys()}

import exifread

with open('prueba03.tif', 'rb') as f:
    tags = exifread.process_file(f)
    for t in tags:
        print(t,": ", tags[t])



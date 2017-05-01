from PIL import Image
import math

def main():
    sample = Image.open('sample_pixel_array.png')
    samplelist = list(sample.getdata())
    #samplelist =[list(x) for x in list(sample.getdata())]

    #for x, index in enumerate(samplelist):
        #print(str(index) + ": " + str(x))

    img260 = sampleImg = Image.open('MapPixel/260.png')
    img261 = sampleImg = Image.open('MapPixel/261.png')
    img262 = sampleImg = Image.open('MapPixel/262.png')
    img263 = sampleImg = Image.open('MapPixel/263.png')
    img2612 = sampleImg = Image.open('MapPixel/2612.png')
    img2613 = sampleImg = Image.open('MapPixel/2613.png')

    #pixel260 = [list(x) for x in list(img260.getdata())]
    pixel260 = list(img260.getdata())
    pixel261 = list(img261.getdata())
    pixel262 = list(img262.getdata())
    pixel263 = list(img263.getdata())
    pixel2612 = list(img2612.getdata())
    pixel2613 = list(img2613.getdata())

    # create return list
    list260 = [[0 for x in range(40)] for x in range(40)]
    list261 = [[0 for x in range(40)] for x in range(40)]
    list262 = [[0 for x in range(40)] for x in range(40)]
    list263 = [[0 for x in range(40)] for x in range(40)]
    list2612 = [[0 for x in range(40)] for x in range(40)]
    list2613 = [[0 for x in range(40)] for x in range(40)]

    # write file for 260
    for index, x in enumerate(pixel260):
        list260[math.floor(index/40)][index%40] = str(samplelist.index(x))
    file = open("MapText/Map_260.txt", "w")
    for x in range(40):
        s = " ".join(list260[x]) + "\n"
        file.write(s)
    file.close()

    # write file for 261
    for index, x in enumerate(pixel261):
        list261[math.floor(index/40)][index%40] = str(samplelist.index(x))
    file = open("MapText/Map_261.txt", "w")
    for x in range(40):
        s = " ".join(list261[x]) + "\n"
        file.write(s)
    file.close()

    # write file for 262
    for index, x in enumerate(pixel262):
        list262[math.floor(index/40)][index%40] = str(samplelist.index(x))
    file = open("MapText/Map_262.txt", "w")
    for x in range(40):
        s = " ".join(list262[x]) + "\n"
        file.write(s)
    file.close()

    # write file for 263
    for index, x in enumerate(pixel263):
        list263[math.floor(index/40)][index%40] = str(samplelist.index(x))
    file = open("MapText/Map_263.txt", "w")
    for x in range(40):
        s = " ".join(list263[x]) + "\n"
        file.write(s)
    file.close()

    # write file for 2612
    for index, x in enumerate(pixel2612):
        list2612[math.floor(index/40)][index%40] = str(samplelist.index(x))
    file = open("MapText/Map_2612.txt", "w")
    for x in range(40):
        s = " ".join(list2612[x]) + "\n"
        file.write(s)
    file.close()

    # write file for 2613
    for index, x in enumerate(pixel2613):
        list2613[math.floor(index/40)][index%40] = str(samplelist.index(x))
    file = open("MapText/Map_2613.txt", "w")
    for x in range(40):
        s = " ".join(list2613[x]) + "\n"
        file.write(s)
    file.close()


main()
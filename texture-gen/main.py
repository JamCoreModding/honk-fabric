# I am not a Python developer.
# Most of this is from copilot.
import json
import os
import requests
from PIL import Image
from io import BytesIO


def get_item_texture(item: str):
    '''Get a pillow image from a URL'''
    response = requests.get(
        f'https://github.com/InventivetalentDev/minecraft-assets/blob/1.20.1/assets/minecraft/textures/item/{item}.png?raw=true')
    return Image.open(BytesIO(response.content))


def is_outlining_pixel(image: Image.Image, x: int, y: int):
    '''Returns true if the given pixel location has a neighbour with a 0 alpha value'''
    if x > 0:
        if image.getpixel((x - 1, y))[3] == 0:
            return True
    if x < image.size[0] - 1:
        if image.getpixel((x + 1, y))[3] == 0:
            return True
    if y > 0:
        if image.getpixel((x, y - 1))[3] == 0:
            return True
    if y < image.size[1] - 1:
        if image.getpixel((x, y + 1))[3] == 0:
            return True
    return False


def get_modal_colors(image: Image.Image):
    '''Gets the top 4 most commonly occurring colours in an image, besides black, white, transparent, and outlining pixels.'''
    colors = {}
    for x in range(image.size[0]):
        for y in range(image.size[1]):
            pixel = image.getpixel((x, y))
            if pixel[3] == 0:
                continue
            if pixel[0] == pixel[1] == pixel[2] == 255:
                continue
            if pixel[0] == pixel[1] == pixel[2] == 0:
                continue
            if is_outlining_pixel(image, x, y):
                continue
            if pixel in colors:
                colors[pixel] += 1
            else:
                colors[pixel] = 1
    return sorted(colors, key=colors.get, reverse=True)[:4]


def get_color_difference(color1: tuple[int, int, int, int], color2: tuple[int, int, int, int]):
    '''Gets the difference between two colours (lower is better)'''
    return abs(color1[0] - color2[0]) + abs(color1[1] - color2[1]) + abs(color1[2] - color2[2])


def get_closest_color(search_color: tuple[int, int, int, int], colors: list[tuple[int, int, int, int]]):
    '''Gets the closest color to the given color'''
    return min(colors, key=lambda color: get_color_difference(search_color, color))


def create_honk_texture(item: str):
    '''Creates a honk texture for the given item'''
    template = Image.open(r"./honk_template.png")
    colors_to_replace = [(226, 226, 226, 255), (255, 255, 255, 255), (211, 211, 211, 255), (198, 198, 198, 255)]
    modal_colors = get_modal_colors(get_item_texture(item))
    print(modal_colors)
    color_mapping = {}
    for i, replace in enumerate(colors_to_replace):
        print(f'{replace} {get_closest_color(replace, modal_colors)}')
        color_mapping[replace] = get_closest_color(replace, modal_colors)
        del modal_colors[modal_colors.index(color_mapping[replace])]
    print(color_mapping)

    for x in range(template.size[0]):
        for y in range(template.size[1]):
            pix = template.getpixel((x, y))
            if pix in color_mapping:
                template.putpixel((x, y), color_mapping[pix])

    return template


def get_honk_types():
    '''Gets the honk types from the honk_types directory.'''
    type_dir = "../src/main/resources/data/honk/honk_types"
    for file in os.listdir(type_dir):
        if file.endswith(".json"):
            with open(os.path.join(type_dir, file)) as f:
                yield json.load(f)['output'].replace('minecraft:', '')


def create_single(item: str):
    '''Creates a single honk texture for the given item'''
    print(f'Creating honk texture for {item}')
    text = create_honk_texture(item)
    text.save(f'../src/main/resources/assets/honk/textures/entity/honk/{item}.png')


def create_all():
    '''Creates all honk textures'''
    for item in get_honk_types():
        create_single(item)


if __name__ == '__main__':
    if os.sys.argv[1] == 'all':
        create_all()
    elif len(os.sys.argv) == 1:
        create_single(os.sys.argv[1])

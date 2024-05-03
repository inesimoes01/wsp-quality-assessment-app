import cv2
import io
import matplotlib.pyplot as plt

from Distortion import *
from Calculated_Statistics import *
from Droplet import *
from Statistics import *
from Util import *

image_undistorted = []
image_segmented = []
def main(file_path, width, height):

    # content_file = io.BytesIO(content)
    # nparr = np.frombuffer(content, np.uint8)

    # get name of the file
    file_path_parts = file_path.split("/")
    parts = file_path[len(file_path_parts)].split(".")
    filename = parts[0]

    try:
        image = cv2.imread(file_path, cv2.IMREAD_GRAYSCALE)

        # generate undistorted image
        dist = Distortion(image, "0", save_photo=False)
        no_paper = dist.noPaper
        if no_paper: return 0, 0, 0, 0
        image_undistorted = dist.undistorted_image


        calc_stats = Calculated_Statistics(dist.undistorted_image, "0", save_images=False, create_masks= False)

        #return 1, 1, 1, 1
        return calc_stats.stats.no_droplets, calc_stats.stats.coverage_percentage, calc_stats.stats.rsf_value, calc_stats.stats.vmd_value

    except Exception as e:
        print("An error occurred:", e)
        return 0, 0.0, 0.0, 1.0
def get_segmented_image():


    plt.plot(image_segmented)
    segm = io.BytesIO()
    plt.savefig(segm, format="jpg")

    return segm.getvalue()
def get_undistorted_image():
    plt.plot(image_undistorted)
    undis = io.BytesIO()
    plt.savefig(undis, format="jpg")

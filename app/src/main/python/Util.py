import os
import shutil
from matplotlib import pyplot as plt

def delete_old_files(file_path):
    for filename in os.listdir(file_path):
        path = os.path.join(file_path, filename)
        if os.path.isfile(path):
            os.remove(path)

def delete_folder_contents(folder_path):
    for filename in os.listdir(folder_path):
        file_path = os.path.join(folder_path, filename)
        try:
            if os.path.isfile(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path):
                shutil.rmtree(file_path)
        except Exception as e:
            print(f"Failed to delete {file_path}. Reason: {e}")

def create_folders(file_path):
    if not os.path.exists(file_path):
        os.makedirs(file_path)

def plotThreeImages(image1, image2, image3):
    plt.close('all')
    fig, axes = plt.subplots(1, 3, figsize=(16, 8))

    axes[0].imshow(image1)
    #axes[0].axis('off')
    axes[0].set_xlabel("X (pixels)")
    axes[0].set_ylabel("Y (pixels)")

    axes[1].imshow(image2)
    #axes[1].axis('off')
    axes[1].set_xlabel("X (pixels)")
    axes[1].set_ylabel("Y (pixels)")

    axes[2].imshow(image3)
    #axes[1].axis('off')
    axes[2].set_xlabel("X (pixels)")
    axes[2].set_ylabel("Y (pixels)")

    plt.show()

def plotALOTImages(images):
    fig, axes = plt.subplots(2, 2, figsize=(10,10))
    for i, ax in enumerate(axes.flat):
        ax.imshow(images[i])
        ax.axis('off')
    plt.tight_layout()
    plt.show()



def plotTwoImages(image1, image2):
    plt.close('all')
    fig, axes = plt.subplots(1, 2, figsize=(16, 8))

    axes[0].imshow(image1)
    #axes[0].axis('off')
    axes[0].set_xlabel("X (pixels)")
    axes[0].set_ylabel("Y (pixels)")

    axes[1].imshow(image2)
    #axes[1].axis('off')
    axes[1].set_xlabel("X (pixels)")
    axes[1].set_ylabel("Y (pixels)")

    plt.show()

def plotFourImages(image1, image2, image3, image4):
    plt.close('all')
    fig, axes = plt.subplots(2, 2, figsize=(16, 8))

    axes[0].imshow(image1)
    #axes[0].axis('off')
    axes[0].set_xlabel("X (pixels)")
    axes[0].set_ylabel("Y (pixels)")

    axes[1].imshow(image2)
    #axes[1].axis('off')
    axes[1].set_xlabel("X (pixels)")
    axes[1].set_ylabel("Y (pixels)")

    axes[2].imshow(image3)
    #axes[1].axis('off')
    axes[2].set_xlabel("X (pixels)")
    axes[1].set_ylabel("Y (pixels)")

    axes[3].imshow(image4)
    #axes[1].axis('off')
    axes[3].set_xlabel("X (pixels)")
    axes[3].set_ylabel("Y (pixels)")


    plt.show()



const storyServices = require("../../services/storyService");
const path = require('path');
const bucket = require('../../config/imageConfig');

const {
  validateStorySchema,
} = require("../../validations/story");

const handlerAddStory = async (req, res, next) => {
  try {
    validateStorySchema(req.body);
    console.log(req.user.id)

    if (!req.file) {
      return res.status(400).json({
        status: "Failed",
        message: "Tidak ada file yang diunggah",
      });
    }
    console.log("test")

    console.log('File uploaded:', req.file); 

    // Check if buffer has data
    if (!req.file.buffer || req.file.buffer.length === 0) {
      console.error('Buffer is empty or undefined');
      return res.status(400).json({
        status: "Failed",
        message: "Buffer is empty or undefined",
      });
    }

    console.log('Buffer length:', req.file.buffer.length); 

    // Generate file path
    const filePath = `${Date.now()}${path.extname(req.file.originalname)}`;

    // Create a blob reference
    const blob = bucket.file(filePath);

    // Create a Promise to handle the blob stream
    const blobStreamPromise = new Promise((resolve, reject) => {
      const blobStream = blob.createWriteStream({
        resumable: false,  
        contentType: req.file.mimetype
      });

      blobStream.on('error', (err) => {
        console.error('Blob stream error:', err);  // Logging error
        reject(err);
      });

      blobStream.on('finish', () => {
        const publicUrl = `https://storage.googleapis.com/${bucket.name}/${blob.name}`;
        resolve(publicUrl);
      });

      console.log('Writing file to blob stream');  // Logging untuk debug
      blobStream.end(req.file.buffer);
    });

    // Wait for the blob stream to finish and get the public URL
    const publicUrl = await blobStreamPromise;

    console.log('Public URL:', publicUrl);  // Logging untuk debug


    const story = await storyServices.postStory(req.body, req.user.id, publicUrl);
    res.status(201).json({
      status: "success",
      message: "Successfully Post Story",
      data: story,
    });
  } catch (err) {
    next(err);
  }
}

const handlerGetAllStories = async (req, res, next) => {
  try {
    const story = await storyServices.getAllStory();

    res.status(200).json({
      status: "success",
      message: "Successfully Get All Stories",
      data: story,
    });
  } catch (err) {
    next(err);
  }
}

module.exports = {
  handlerAddStory,
  handlerGetAllStories,
}
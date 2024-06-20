const Joi = require("joi");

const storyPostingSchema = Joi.object({
  title: Joi.string().required(),
  content: Joi.string().required(),
}).unknown();

module.exports = {
  storyPostingSchema,
};
var webpack = require('webpack');
var path = require("path"),
  CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  entry: {
    babelPolyfill: 'babel-polyfill',
    controller: path.resolve(__dirname, "./src/main/webapp/js/controller.js"),
    app: path.resolve(__dirname, "./src/main/webapp/js/app.jsx")
  },
  output: {
    path: path.resolve(__dirname, "./target/static"),
    filename: "[name].bundle.js"
  },
  devtool: 'source-map',
  module: {
    preLoaders: [
      { test: /\.jsx?$/, loader: 'eslint', exclude: /node_modules/ }
    ],
    loaders: [
      { test: /\.css$/, loader: "style!css" },
      { test: /\.less$/, loader: "style!css!less" },
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        loader: 'babel',
        query: {
          presets: ['react', 'es2015'],
          plugins: ['transform-object-rest-spread'],
        }
      },
      { test: /\.(jpe?g|png|gif|svg)$/i, loader: 'url?limit=10000!img?progressive=true' },
      { test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/font-woff' },
      { test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=application/octet-stream' },
      { test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: 'file' },
      { test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: 'url?limit=10000&mimetype=image/svg+xml' }
    ]
  },
  resolve: {
    extensions: ['', '.js', '.jsx']
  },
  eslint: {
    configFile: './.eslintrc',
    failOnWarning: false,
    failOnError: true,
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'), // Tells React to build in either dev or prod modes. https://facebook.github.io/react/downloads.html (See bottom)
      __DEV__: true
    }),
    new CopyWebpackPlugin([{
      from: './src/main/webapp/html/app.html'
    }]),
    new CopyWebpackPlugin([{
      from: './src/main/webapp/html/controller.html'
    }]),
    new CopyWebpackPlugin([{
      from: './src/main/webapp/bundle.json'
    }]),
    new CopyWebpackPlugin([{
      from: './src/main/webapp/img', to: 'img'
    }]),
  ],
  devServer: {
    contentBase: path.resolve(__dirname, './target/static'),
    port: 4000,
    inline: true,
    headers: {
      "Access-Control-Allow-Origin": "*"
    }
  }
};

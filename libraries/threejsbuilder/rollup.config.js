import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import { terser } from 'rollup-plugin-terser';

export default {
  // Entry point of your project
  input: 'index.js',

  // Output configuration
  output: {
    file: 'dist/three.min.js', // Output file
    format: 'umd', // Universal Module Definition (works in browsers and Node.js)
    name: 'THREE', // Global variable name exposed in the browser
    sourcemap: true // Generate sourcemaps for debugging
  },

  // Plugins to process the bundle
  plugins: [
    resolve({
      // Specify file extensions to resolve
      extensions: ['.js'],
      // Prefer node_modules over built-in Node.js modules
      preferBuiltins: false,
      // Ensure Three.js and its examples are resolved correctly
      moduleDirectories: ['node_modules']
    }),
    commonjs({
      // Handle any CommonJS modules that might sneak in
      include: 'node_modules/**'
    }),
    terser({
      // Minification options
      compress: {
        drop_console: false, // Keep console logs (set to true to remove them)
        pure_funcs: [] // Add functions to treat as pure if needed
      },
      mangle: true, // Mangle variable names for smaller size
      output: {
        comments: false // Remove comments in the output
      }
    })
  ],

  // External dependencies (none in this case, since we bundle everything)
  external: [],

  // Optional: Watch mode for development (not needed for production build)
  watch: {
    include: 'index.js',
    exclude: 'node_modules/**'
  }
};
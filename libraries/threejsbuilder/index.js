import * as THREE from 'three';

// Controls
import { DragControls } from 'three/examples/jsm/controls/DragControls.js';
import { FirstPersonControls } from 'three/examples/jsm/controls/FirstPersonControls.js';
import { FlyControls } from 'three/examples/jsm/controls/FlyControls.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { PointerLockControls } from 'three/examples/jsm/controls/PointerLockControls.js';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls.js';
import { TransformControls } from 'three/examples/jsm/controls/TransformControls.js';

// Loaders
import { BVHLoader } from 'three/examples/jsm/loaders/BVHLoader.js';
import { ColladaLoader } from 'three/examples/jsm/loaders/ColladaLoader.js';
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader.js';
import { EXRLoader } from 'three/examples/jsm/loaders/EXRLoader.js';
import { FBXLoader } from 'three/examples/jsm/loaders/FBXLoader.js';
import { GCodeLoader } from 'three/examples/jsm/loaders/GCodeLoader.js';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { HDRCubeTextureLoader } from 'three/examples/jsm/loaders/HDRCubeTextureLoader.js';
import { KTX2Loader } from 'three/examples/jsm/loaders/KTX2Loader.js';
import { LDrawLoader } from 'three/examples/jsm/loaders/LDrawLoader.js';
import { LottieLoader } from 'three/examples/jsm/loaders/LottieLoader.js';
import { MTLLoader } from 'three/examples/jsm/loaders/MTLLoader.js';
import { OBJLoader } from 'three/examples/jsm/loaders/OBJLoader.js';
import { PCDLoader } from 'three/examples/jsm/loaders/PCDLoader.js';
import { PDBLoader } from 'three/examples/jsm/loaders/PDBLoader.js';
import { PLYLoader } from 'three/examples/jsm/loaders/PLYLoader.js';
import { RGBELoader } from 'three/examples/jsm/loaders/RGBELoader.js';
import { STLLoader } from 'three/examples/jsm/loaders/STLLoader.js';
import { SVGLoader } from 'three/examples/jsm/loaders/SVGLoader.js';
import { TDSLoader } from 'three/examples/jsm/loaders/TDSLoader.js';
import { TGALoader } from 'three/examples/jsm/loaders/TGALoader.js';
import { VOXLoader } from 'three/examples/jsm/loaders/VOXLoader.js';
import { VRMLLoader } from 'three/examples/jsm/loaders/VRMLLoader.js';
import { VTKLoader } from 'three/examples/jsm/loaders/VTKLoader.js';

// Post-processing
import { EffectComposer } from 'three/examples/jsm/postprocessing/EffectComposer.js';
import { RenderPass } from 'three/examples/jsm/postprocessing/RenderPass.js';
import { AfterimagePass } from 'three/examples/jsm/postprocessing/AfterimagePass.js';
import { BloomPass } from 'three/examples/jsm/postprocessing/BloomPass.js';
import { BokehPass } from 'three/examples/jsm/postprocessing/BokehPass.js';
import { ClearPass } from 'three/examples/jsm/postprocessing/ClearPass.js';
import { CubeTexturePass } from 'three/examples/jsm/postprocessing/CubeTexturePass.js';
import { DotScreenPass } from 'three/examples/jsm/postprocessing/DotScreenPass.js';
import { FilmPass } from 'three/examples/jsm/postprocessing/FilmPass.js';
import { GlitchPass } from 'three/examples/jsm/postprocessing/GlitchPass.js';
import { HalftonePass } from 'three/examples/jsm/postprocessing/HalftonePass.js';
import { LUTPass } from 'three/examples/jsm/postprocessing/LUTPass.js';
import { MaskPass } from 'three/examples/jsm/postprocessing/MaskPass.js';
import { OutlinePass } from 'three/examples/jsm/postprocessing/OutlinePass.js';
import { SAOPass } from 'three/examples/jsm/postprocessing/SAOPass.js';
import { SMAAPass } from 'three/examples/jsm/postprocessing/SMAAPass.js';
import { SSAARenderPass } from 'three/examples/jsm/postprocessing/SSAARenderPass.js';
import { SSAOPass } from 'three/examples/jsm/postprocessing/SSAOPass.js';
import { SSRPass } from 'three/examples/jsm/postprocessing/SSRPass.js';
import { ShaderPass } from 'three/examples/jsm/postprocessing/ShaderPass.js';
import { TAARenderPass } from 'three/examples/jsm/postprocessing/TAARenderPass.js';
import { TexturePass } from 'three/examples/jsm/postprocessing/TexturePass.js';
import { UnrealBloomPass } from 'three/examples/jsm/postprocessing/UnrealBloomPass.js';

// Modifiers
import { EdgeSplitModifier } from 'three/examples/jsm/modifiers/EdgeSplitModifier.js';
import { SimplifyModifier } from 'three/examples/jsm/modifiers/SimplifyModifier.js';
import { TessellateModifier } from 'three/examples/jsm/modifiers/TessellateModifier.js';

// Utilities
import * as BufferGeometryUtils from 'three/examples/jsm/utils/BufferGeometryUtils.js';
import * as CameraUtils from 'three/examples/jsm/utils/CameraUtils.js';
import * as GeometryUtils from 'three/examples/jsm/utils/GeometryUtils.js';
import * as SceneUtils from 'three/examples/jsm/utils/SceneUtils.js';
import { ShadowMapViewer } from 'three/examples/jsm/utils/ShadowMapViewer.js';
import * as SkeletonUtils from 'three/examples/jsm/utils/SkeletonUtils.js';
import { UVsDebug } from 'three/examples/jsm/utils/UVsDebug.js';

// Miscellaneous Libraries
import { FontLoader } from 'three/examples/jsm/loaders/FontLoader.js';
import { ImprovedNoise } from 'three/examples/jsm/math/ImprovedNoise.js';
import { Lut } from 'three/examples/jsm/math/Lut.js';
import { MeshoptDecoder } from 'three/examples/jsm/libs/meshopt_decoder.module.js';
import * as Stats from 'three/examples/jsm/libs/stats.module.js';
import { GUI } from 'three/examples/jsm/libs/lil-gui.module.min.js';

// WebXR and Animation
import { XRControllerModelFactory } from 'three/examples/jsm/webxr/XRControllerModelFactory.js';
import { MorphBlendMesh } from 'three/examples/jsm/misc/MorphBlendMesh.js';
import { RollerCoasterGeometry } from 'three/examples/jsm/misc/RollerCoaster.js';
import * as RollerCoasterLerpMaterial from 'three/examples/jsm/misc/RollerCoaster.js';
import * as RollerCoasterShadow from 'three/examples/jsm/misc/RollerCoaster.js';

// Create a new extensible object based on THREE
const THREEW = { ...THREE };

// Assign everything to the new extensible object
Object.assign(THREEW, {
  // Controls
  DragControls,
  FirstPersonControls,
  FlyControls,
  OrbitControls,
  PointerLockControls,
  TrackballControls,
  TransformControls,

  // Loaders
  BVHLoader,
  ColladaLoader,
  DRACOLoader,
  EXRLoader,
  FBXLoader,
  GCodeLoader,
  GLTFLoader,
  HDRCubeTextureLoader,
  KTX2Loader,
  LDrawLoader,
  LottieLoader,
  MTLLoader,
  OBJLoader,
  PCDLoader,
  PDBLoader,
  PLYLoader,
  RGBELoader,
  STLLoader,
  SVGLoader,
  TDSLoader,
  TGALoader,
  VOXLoader,
  VRMLLoader,
  VTKLoader,

  // Post-processing
  EffectComposer,
  RenderPass,
  AfterimagePass,
  BloomPass,
  BokehPass,
  ClearPass,
  CubeTexturePass,
  DotScreenPass,
  FilmPass,
  GlitchPass,
  HalftonePass,
  LUTPass,
  MaskPass,
  OutlinePass,
  SAOPass,
  SMAAPass,
  SSAARenderPass,
  SSAOPass,
  SSRPass,
  ShaderPass,
  TAARenderPass,
  TexturePass,
  UnrealBloomPass,

  // Modifiers
  EdgeSplitModifier,
  SimplifyModifier,
  TessellateModifier,

  // Utilities
  ...BufferGeometryUtils,
  ...CameraUtils,
  ...GeometryUtils,
  ...SceneUtils,
  ShadowMapViewer,
  ...SkeletonUtils,
  UVsDebug,

  // Miscellaneous Libraries
  FontLoader,
  ImprovedNoise,
  Lut,
  MeshoptDecoder,
  ...Stats,
  GUI,

  // WebXR and Animation
  XRControllerModelFactory,
  MorphBlendMesh,
  RollerCoasterGeometry,
  ...RollerCoasterLerpMaterial,
  ...RollerCoasterShadow
});

// Export the extended object
export default THREEW;
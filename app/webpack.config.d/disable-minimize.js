// WORKAROUND: Disables minimization/optimization for faster builds and to prevent hanging

;(function(config) {
    if (config.mode === 'production') {
        config.optimization = config.optimization || {};
        config.optimization.minimize = false;
    }
})(config);
var app = new Vue({
    el: '#rk-app',
    data() {
        return {
            loading: null,
            timer: "",
            currentTime: new Date(),
            time1: '',
            time2: '',
            configMB: {
                header: ['列1', '列2', '列3', '列3'],
                data: [
                    ['行1列1', '行1列2', '行1列3', '行1列3'],
                    ['行2列1', '行2列2', '行2列3', '行1列3'],
                    ['行3列1', '行3列2', '行3列3', '行1列3'],
                    ['行4列1', '行4列2', '行4列3', '行1列3'],
                    ['行5列1', '行5列2', '行5列3', '行1列3'],
                    ['行6列1', '行6列2', '行6列3', '行1列3'],
                    ['行7列1', '行7列2', '行7列3', '行1列3'],
                    ['行8列1', '行8列2', '行8列3', '行1列3'],
                    ['行9列1', '行9列2', '行9列3', '行1列3'],
                    ['行10列1', '行10列2', '行10列3', '行1列3']
                ],
                index: true,
                columnWidth: [50],
                rowNum: 5,
                align: ['center'],
                waitTime: 2000,
                carousel: 'single', // 'single'|'page'
            },
            config1: {
                data: [{
                    name: '周口',
                    value: 55
                }, {
                    name: '南阳',
                    value: 120
                }, {
                    name: '西峡',
                    value: 71
                }, {
                    name: '驻马店',
                    value: 66
                }, {
                    name: '新乡',
                    value: 80
                }, {
                    name: '信阳',
                    value: 35
                }, {
                    name: '漯河',
                    value: 15
                }],
                img: [
                    'icon/1st.png',
                    'icon/2st.png',
                    'icon/3st.png',
                    'icon/4st.png',
                    'icon/5st.png',
                    'icon/6st.png',
                    'icon/7st.png'
                ],
                showValue: true

            },
            config2: {
                data: [
                    ['行1列1行1列1行1列1行1列1'],
                    ['行2行1列1行1列1行1列1列1'],
                    ['行3列行1列1行1列1行1列11'],
                    ['行4列行1列1行1列11'],
                    ['行5列行1列1行1列1行1列1行1列11'],
                    ['行6行1列1列1'],
                    ['行7列1行1列1行1列1行1列1'],
                    ['行8列行1列11'],
                    ['行9列1'],
                    ['行10行1列1行1列1列1']
                ],
                align: ['left'],
                oddRowBGC: '#186885 ',
                evenRowBGC: '#282c34 ',
                rowNum: 7,
                waitTime: 5000,
                carousel: 'page', // 'single'|'page'
            },
            config3: {
                data: [{
                    name: '周口',
                    value: 55
                }, {
                    name: '南阳',
                    value: 120
                }, {
                    name: '西峡',
                    value: 78
                }, {
                    name: '驻马店',
                    value: 66
                }, {
                    name: '新乡',
                    value: 80
                }],
                unit: '单位',
            },
            config4: {
                data: [{
                    name: '周口',
                    value: 55
                }, {
                    name: '南阳',
                    value: 120
                }, {
                    name: '西峡',
                    value: 78
                }, {
                    name: '驻马店',
                    value: 66
                }, {
                    name: '新乡',
                    value: 80
                }]
            }

        }
    },
    methods: {
        zero: function(obj) {
            if (obj < 10) {
                return "0" + obj;
            } else {
                return obj;
            }
        },
        getData: function() {
            this.time1 =
                new Date().getFullYear() +
                "-" +
                this.zero((new Date().getMonth() + 1)) +
                "-" +
                this.zero(new Date().getDate())
            this.time2 =
                this.zero(new Date().getHours()) +
                ":" +
                this.zero(new Date().getMinutes()) +
                ":" +
                this.zero(new Date().getSeconds());
        },
        getServerData: function(url, type, param, success, isAsync) {
            debugger
            var that = this;
            var params = {};
            var token = localStorage.getItem('token');
            var baseUrl = 'http://localhost:8000/wms/';
            type === 'get' ? params = this.JSON(param) : params = JSON.stringify(param);
            var async = true;
            this.isNull(isAsync) ? async = isAsync : async = true;
            console.log('token:', token, ' url:', url, ' params:', JSON.stringify(params));
            $.ajax({
                type: type,
                dataType: "json",
                timeout: 1000 * 60 * 60,
                data: params,
                headers: {
                    "Accept": "*/*",
                    "access-token": token
                },
                contentType: "application/json",
                url: baseUrl + url,
                async: async,
                success: function(res) {
                    if (res.code === 10006) {
                        this.tips('访问令牌参数失效,请重新登录!', 'error', 1000, function() {
                            that.$router.push({ name: 'login' })
                        });
                        return false
                    }
                    success(res);
                },
                error: function(err) {
                    this.tips(err.statusText === 'timeout' ? '请求超时!' : err || '未知的错误!', 'error', 1000, function() {
                        this.hideOverlay();
                    });
                    console.error(err.statusText, err || '未知的错误!');
                }
            });
        },
        showOverlay: function(content) {
            var text = '';
            if (content) {
                text = content
            }
            this.loading = this.$loading({
                lock: true,
                text: text || '',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
        },
        hideOverlay: function() {
            if (this.loading) {
                this.loading.close();
            }
        },
        tips: function(message, type, time, end) {
            var duration = 1500;
            if (time) {
                duration = time
            }

            this.$message({
                showClose: true,
                dangerouslyUseHTMLString: true,
                message: message,
                type: type,
                duration: duration,
                onClose: end
            });
        },
        isNull: function(val, type) {
            if (type === '') {
                if (val === null || val === undefined) {
                    return false
                } else {
                    return true
                }
            } else {
                if (val === '' || val === null || val === undefined) {
                    return false
                } else {
                    return true
                }
            }
        },
        JSON: function(data) {
            if (typeof data === 'object') {
                return JSON.parse(JSON.stringify(data))
            } else if (typeof data === 'string') {
                return JSON.parse(data)
            }
        },
    },
    created: function() {
        $.ajax({
            type: "get",
            url: "/in/taskInfo/getTaskinfoForWcs",
            data: {

            },
            success: function(r) {
                debugger
                if (r.code == 200) {

                } else {

                }
            }
        });

        var that = this;
        this.getData();
        this.timer = setInterval(function() {
            that.getData();
        }, 1000);

        this.getServerData('/in/billInMaster/findList', 'get', {}, function(ret) {
            if (ret.code === 200) {
                var list = ret.rows;
            } else {
                this.tips(ret.message || '服务器请求失败，稍后再试！', 'error');
            }
        })
    },
    mounted: function() {

    },
    beforeDestroy: function() {
        if (this.timer) {
            clearInterval(this.timer); // 在Vue实例销毁前，清除我们的定时器
        }
    }
})
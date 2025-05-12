### macOs 13.7 m2 pro work

###### mac基础配置：

手势（触控板 辅助功能指针控制）、屏保、电池、触发角锁屏、键盘输入法、日期与时间、显示器高级设置防止睡眠、控制中心电池显示百分比

###### iterm2：

**homebrew安装**

**hostname修改**
sudo scutil --set HostName [NewHostName]
关闭终端应用程序并重新启动你的 Mac，这样更改才能生效


iTerm2安装配置使用指南 https://zhuanlan.zhihu.com/p/550022490

oh my zsh安装、zsh插件 git zsh-syntax-highlighting zsh-autosuggestions

**cmd窗口显示全路径**
vim 对应主题文件，source 对应修改文件使其生效
PROMPT="%(?:%{$fg_bold[green]%}➜ :%{$fg_bold[red]%}➜ ) %{$fg[cyan]%}%d%{$reset_color%}"

###### jdk8安装、maven安装：

JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
PATH=$JAVA_HOME/bin:$PATH:.
CLASSPATH=$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/dt.jar:.
export JAVA_HOME
export PATH
export CLASSPATH

export MAVEN_HOME=/Users/xxx/maven/apache-maven-3.9.2
export PATH=$PATH:$MAVEN_HOME/bin

###### 软件安装：

谷歌、搜狗输入法、OmniGraffle、sublime、The Unarchiver、XMind8、StarUML、idea、XMind8、网易云音乐

IDEA 2022.3.2 最优开发配置： https://www.cnblogs.com/pkukhq/p/17172214.html
提升工作效率，IDEA配置优化总结： https://juejin.cn/post/7323883238896353299
                            https://juejin.cn/post/7039295381213020197#heading-0

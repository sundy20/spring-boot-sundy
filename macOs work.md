### macOs 13.4 m2 pro 工作环境配置

###### mac基础配置：

手势（触控板 辅助功能指针控制）、屏保、电池、触发角锁屏、键盘输入法、日期与时间、显示器高级设置防止睡眠、控制中心电池显示百分比

###### iterm2：

hostname修改
sudo scutil --set HostName [NewHostName]
关闭终端应用程序并重新启动你的 Mac，这样更改才能生效

iTerm2安装配置使用指南 https://zhuanlan.zhihu.com/p/550022490
oh my zsh安装、zsh插件 git zsh-syntax-highlighting zsh-autosuggestions
显示当前文件夹
PROMPT="%(?:%{$fg_bold[green]%}➜ :%{$fg_bold[red]%}➜ ) %{$fg[cyan]%}%d%{$reset_color%}"
PROMPT+=' $(git_prompt_info)'

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
TeamFile备份文件夹下载
IDEA 2022.3.2 最优开发配置https://www.cnblogs.com/pkukhq/p/17172214.html

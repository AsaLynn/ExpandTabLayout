# ExpandTabLayout
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-FlycoTabLayout-green.svg?style=true)](https://android-arsenal.com/details/1/2756)

一个Android TabLayout库,目前有3个TabLayout

* SlidingTabLayout:参照[PagerSlidingTabStrip](https://github.com/jpardogo/PagerSlidingTabStrip)进行大量修改.
    * 新增部分属性
    * 新增支持多种Indicator显示器
    * 新增支持未读消息显示
    * 新增方法for懒癌患者
    
```java
/** 关联ViewPager,用于不想在ViewPager适配器中设置titles数据的情况 */
public void setViewPager(ViewPager vp, String[] titles)

/** 关联ViewPager,用于连适配器都不想自己实例化的情况 */
public void setViewPager(ViewPager vp, String[] titles, FragmentActivity fa, ArrayList<Fragment> fragments) 
```

* CommonTabLayout:不同于SlidingTabLayout对ViewPager依赖,它是一个不依赖ViewPager可以与其他控件自由搭配使用的TabLayout.
    * 支持多种Indicator显示器,以及Indicator动画
    * 支持未读消息显示
    * 支持Icon以及Icon位置
    * 新增方法for懒癌患者
    
```java
/** 关联数据支持同时切换fragments */
public void setTabData(ArrayList<CustomTabEntity> tabEntitys, FragmentManager fm, int containerViewId, ArrayList<Fragment> fragments)
```

* SegmentTabLayout

## Demo
![](https://github.com/H07000223/FlycoTabLayout/blob/master/preview_1.gif)

![](https://github.com/H07000223/FlycoTabLayout/blob/master/preview_2.gif)

![](https://github.com/H07000223/FlycoTabLayout/blob/master/preview_3.gif)

![](https://github.com/H07000223/FlycoTabLayout/blob/master/image/image01)

![](https://github.com/H07000223/FlycoTabLayout/blob/master/image/image02)

![](https://github.com/H07000223/FlycoTabLayout/blob/master/image/image03)

![](https://github.com/H07000223/FlycoTabLayout/blob/master/image/image04)

![](https://github.com/H07000223/FlycoTabLayout/blob/master/image/image05)

## Gradle

```
dependencies{
    implementation 'com.zxn.tablayout:expand-tablayout:1.0.6'
    implementation 'com.zxn.tablayout:expand-tablayout:1.0.5'
}
```

## Attributes

|name|format|description|
|:---:|:---:|:---:|
| tl_indicator_color | color |设置显示器颜色
| tl_indicator_height | dimension |设置显示器高度
| tl_indicator_width | dimension |设置显示器固定宽度
| tl_indicator_margin_left | dimension |设置显示器margin,当indicator_width大于0,无效
| tl_indicator_margin_top | dimension |设置显示器margin,当indicator_width大于0,无效
| tl_indicator_margin_right | dimension |设置显示器margin,当indicator_width大于0,无效
| tl_indicator_margin_bottom | dimension |设置显示器margin,当indicator_width大于0,无效 
| tl_indicator_corner_radius | dimension |设置显示器圆角弧度
| tl_indicator_gravity | enum |设置显示器上方(TOP)还是下方(BOTTOM),只对常规显示器有用
| tl_indicator_style | enum |设置显示器为常规(NORMAL)或三角形(TRIANGLE)或背景色块(BLOCK)
| tl_underline_color | color |设置下划线颜色
| tl_underline_height | dimension |设置下划线高度
| tl_underline_gravity | enum |设置下划线上方(TOP)还是下方(BOTTOM)
| tl_divider_color | color |设置分割线颜色
| tl_divider_width | dimension |设置分割线宽度
| tl_divider_padding |dimension| 设置分割线的paddingTop和paddingBottom
| tl_tab_padding |dimension| 设置tab的paddingLeft和paddingRight
| tl_tab_space_equal |boolean| 设置tab大小等分
| tl_tab_width |dimension| 设置tab固定大小
| tl_textsize |dimension| 设置字体大小
| tl_textSelectColor |color| 设置字体选中颜色
| tl_textUnselectColor |color| 设置字体未选中颜色
| tl_textBold |boolean| 设置字体加粗
| tl_iconWidth |dimension| 设置icon宽度(仅支持CommonTabLayout)
| tl_iconHeight |dimension|设置icon高度(仅支持CommonTabLayout)
| tl_iconVisible |boolean| 设置icon是否可见(仅支持CommonTabLayout)
| tl_iconGravity |enum| 设置icon显示位置,对应Gravity中常量值,左上右下(仅支持CommonTabLayout)
| tl_iconMargin |dimension| 设置icon与文字间距(仅支持CommonTabLayout)
| tl_indicator_anim_enable |boolean| 设置显示器支持动画(only for CommonTabLayout)
| tl_indicator_anim_duration |integer| 设置显示器动画时间(only for CommonTabLayout)
| tl_indicator_bounce_enable |boolean| 设置显示器支持动画回弹效果(only for CommonTabLayout)
| tl_indicator_width_equal_title |boolean| 设置显示器与标题一样长(only for SlidingTabLayout)

## Dependence
*   [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids)
*   [FlycoRoundView](https://github.com/H07000223/FlycoRoundView)

## Thanks
*   [PagerSlidingTabStrip](https://github.com/jpardogo/PagerSlidingTabStrip)
*   [FlycoTabLayout](https://github.com/H07000223/FlycoTabLayout)

## 标签

expand-tablayout:1.0.4
```
git tag -a v1.0.4 -m 'expand-tablayout1.0.4:修复bug'
git push origin v1.0.4
git tag
```

expand-tablayout:1.0.3
```
git tag -a v1.0.3 -m 'expand-tablayout1.0.3:增加设置消息最大值方法.'
git push origin v1.0.3
git tag
```

expand-tablayout:1.0.2
```
git tag -a v1.0.2 -m 'expand-tablayout1.0.2:'
git push origin v1.0.2
git tag
```

## 变更

```
2、新增设置tab被选中以及未被选中的文字大小，大小的变化会在ViewPager滑动的时候自动变化：

<attr name="tl_textSelectSize" />
<attr name="tl_textUnSelectSize" />
3、标题默认默认是文字居中，可以修改gravity和margin属性，设置在tab中的位置：

<attr name="tl_tab_marginTop" />
<attr name="tl_tab_marginBottom" />
<attr name="tl_tab_gravity" />
4、请务必重写PagerAdapter.getItemPosition()方法，根据object返回正确的位置信息，因为需要通过此方法找到对应位置的SlidingTab，进行文字样式切换：

 @Override
public int getItemPosition(@NonNull Object object) {
    // PagerAdapter的默认实现，请返回正确的位置信息
    return PagerAdapter.POSITION_NONE;
}
1.1.1新增

新增自定义属性:是否开启文字的图片镜像 ，解决SlidingScaleTabLayou文字变化抖动的问题：

<attr name="tl_openTextDmg" format="boolean"/>
```
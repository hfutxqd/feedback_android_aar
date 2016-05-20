# feedback_android_aar
feedback反馈类库
## 基本使用方法
在Activity中:   

    Feedback feedback = new Feedback(this, false);
    feedback.setDescription("这是描述");
    feedback.setUserMail("me@imxqd.xyz");
    // ... 
    feedback.submit(new Feedback.SubmitCallBack() {
         @Override
         public boolean onPreSubmit() {
             return true;
         }

         @Override
         public void onSubmitted(boolean result) {
             System.out.println("onSubmitted:"+result);
         }
     });


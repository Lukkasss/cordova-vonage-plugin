#import <Cordova/CDV.h>
#import <OpenTok/OpenTok.h>

@interface VonagePlugin : CDVPlugin
@property (nonatomic, strong) OTSession *session;
@end

@implementation VonagePlugin

- (void)startSession:(CDVInvokedUrlCommand*)command {
    NSString* apiKey = [command.arguments objectAtIndex:0];
    NSString* sessionId = [command.arguments objectAtIndex:1];
    NSString* token = [command.arguments objectAtIndex:2];

    self.session = [[OTSession alloc] initWithApiKey:apiKey sessionId:sessionId delegate:self];
    [self.session connectWithToken:token error:nil];

    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Sessão iniciada!"];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}
@end

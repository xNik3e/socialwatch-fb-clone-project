<?php
use Psr\Http\Message\ResponseInterface;
use Psr\Http\Message\ServerRequestInterface;
use Psr\Http\Server\RequestHandlerInterface;
use Slim\Factory\AppFactory;
use Slim\Psr7\Response;

require_once __DIR__ . '/../vendor/autoload.php';
require_once __DIR__ . '/../middleware/BeforeMiddleWare.php';
require_once __DIR__ . '/../middleware/AfterMiddleWare.php';


$app = AppFactory::create();
$app->setBasePath('/socialwatch/public');

$app->addRoutingMiddleware();
$app->addBodyParsingMiddleware();






// Define Custom Error Handler
$customErrorHandler = function (
  ServerRequestInterface $request,
  Throwable $exception,
  bool $displayErrorDetails,
  bool $logErrors,
  bool $logErrorDetails,
  ?LoggerInterface $logger = null
) use ($app) {
  $payload = array();
  $payload['status'] = $exception->getCode();
  $payload['message'] = $exception->getMessage();

  $response = $app->getResponseFactory()->createResponse();
  $response->getBody()->write(
      json_encode($payload)
  );

  return $response->withHeader('Content-Type', 'application/json')
    ->withStatus($exception->getCode() != 0 ? $exception->getCode() : 500);
};

// Add Error Middleware
$errorMiddleware = $app->addErrorMiddleware(true, true, true);
$errorMiddleware->setDefaultErrorHandler($customErrorHandler);


$app->add(new BeforeMiddleWare());
//$app->add(new AfterMiddleWare());


require_once __DIR__ .'/../app/user.php';
require_once __DIR__ .'/../app/utils.php';
require_once __DIR__ .'/../app/post.php';
require_once __DIR__ .'/../app/friend.php';



// Run app
$app->run();
?>